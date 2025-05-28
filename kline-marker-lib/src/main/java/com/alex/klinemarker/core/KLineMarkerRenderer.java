package com.alex.klinemarker.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.MarkerData;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * K线标记渲染器
 * 负责在K线图上绘制各种类型的标记
 */
public class KLineMarkerRenderer<T> implements IMarker {

    private static final String TAG = "KLineMarkerRenderer";
    private static final boolean DEBUG = false; // 控制日志输出，发布时设为false

    private final Context context;
    private final CombinedChart chart;
    private final KLineDataAdapter<T> dataAdapter;
    private final MarkerConfig config;

    // 绘制相关的Paint对象
    private Paint markerPaint;
    private Paint textPaint;
    private Paint dashLinePaint;
    private Paint numberTextPaint;
    private Paint linePaint;

    // 数据
    private List<T> klineData;
    private List<MarkerData> markers;
    private final Map<String, MarkerData> dateToMarkerMap;

    // 屏幕密度
    private final float density;

    // 性能优化：复用对象
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public KLineMarkerRenderer(Context context, CombinedChart chart,
                               KLineDataAdapter<T> dataAdapter, MarkerConfig config) {
        this.context = context;
        this.chart = chart;
        this.dataAdapter = dataAdapter;
        this.config = config != null ? config : new MarkerConfig();
        this.density = context.getResources().getDisplayMetrics().density;
        this.dateToMarkerMap = new HashMap<>();

        // 初始化Paint对象
        initPaints();
    }

    /**
     * 初始化绘制相关的Paint对象
     */
    private void initPaints() {
        // 标记背景画笔
        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setStyle(Paint.Style.FILL);

        // 标记文字画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(config.getTextColor());
        textPaint.setTextSize(config.getTextSize() * density);
        textPaint.setTypeface(config.getTextTypeface());
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 虚线画笔
        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setStyle(Paint.Style.STROKE);
        dashLinePaint.setStrokeWidth(config.getLineWidth() * density);
        dashLinePaint.setPathEffect(new DashPathEffect(
                new float[]{config.getDashLength() * density, config.getDashGap() * density}, 0));

        // 数字标记文字画笔
        numberTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberTextPaint.setColor(config.getNumberTextColor());
        numberTextPaint.setTextSize(config.getTextSize() * density);
        numberTextPaint.setTypeface(config.getTextTypeface());
        numberTextPaint.setTextAlign(Paint.Align.LEFT);

        // 引出线画笔
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFF666666); // 灰色引出线
        linePaint.setStrokeWidth(1.5f * density);
    }

    /**
     * 设置K线数据
     *
     * @param klineData K线数据列表
     */
    public void setKLineData(List<T> klineData) {
        this.klineData = klineData;
    }

    /**
     * 设置标记数据
     *
     * @param markers 标记数据列表
     */
    public void setMarkers(List<MarkerData> markers) {
        this.markers = markers;
        updateDateToMarkerMap();
    }

    /**
     * 更新日期到标记的映射关系，提高查找性能
     */
    private void updateDateToMarkerMap() {
        dateToMarkerMap.clear();
        if (markers != null) {
            for (MarkerData marker : markers) {
                if (marker.getDate() != null) {
                    String dateStr = dateFormat.format(marker.getDate());
                    dateToMarkerMap.put(dateStr, marker);
                }
            }
        }
    }

    /**
     * 绘制所有标记
     */
    public void drawMarkers(Canvas canvas) {
        if (klineData == null || markers == null || markers.isEmpty()) {
            return;
        }

        if (DEBUG) Log.d(TAG, "drawMarkers called, markers size: " + markers.size());

        // 获取图表可见区域的时间范围
        float minTime = chart.getLowestVisibleX();
        float maxTime = chart.getHighestVisibleX();

        // 获取图表边界
        float contentTop = chart.getViewPortHandler().contentTop();
        float contentBottom = chart.getViewPortHandler().contentBottom();
        float leftBoundary = chart.getViewPortHandler().contentLeft();
        float rightBoundary = chart.getViewPortHandler().contentRight();

        // 计算安全可视区域
        float markerSizePx = config.getMarkerSize() * density;
        float safeTopY = contentTop + markerSizePx * 1.5f;
        float safeBottomY = contentBottom - markerSizePx * 1.5f;
        float safeLeftX = leftBoundary + markerSizePx;
        float safeRightX = rightBoundary - markerSizePx;

        // 遍历K线数据，查找对应的标记
        for (int i = 0; i < klineData.size(); i++) {
            T klineEntry = klineData.get(i);
            float xValue = dataAdapter.getXValue(klineEntry);

            // 检查是否在可见范围内
            if (xValue < minTime || xValue > maxTime) {
                continue;
            }

            // 查找对应的标记
            String dateStr = dateFormat.format(dataAdapter.getDate(klineEntry));
            MarkerData marker = dateToMarkerMap.get(dateStr);

            if (marker != null && marker.getType() != MarkerData.MarkerType.NONE) {
                drawSingleMarker(canvas, klineEntry, marker, i, safeTopY, safeBottomY, safeLeftX, safeRightX);
            }
        }
    }

    /**
     * 绘制单个标记
     */
    private void drawSingleMarker(Canvas canvas, T klineEntry, MarkerData marker, int index,
                                  float safeTopY, float safeBottomY, float safeLeftX, float safeRightX) {

        float xValue = dataAdapter.getXValue(klineEntry);
        float high = dataAdapter.getHigh(klineEntry);
        float low = dataAdapter.getLow(klineEntry);

        // 设置标记颜色
        int markerColor = getMarkerColor(marker);
        markerPaint.setColor(markerColor);
        dashLinePaint.setColor(markerColor);

        // 计算标记位置
        MarkerPosition position = calculateMarkerPosition(klineEntry, marker, index,
                safeTopY, safeBottomY, safeLeftX, safeRightX);

        // 绘制连接线
        drawConnectionLine(canvas, position, marker.getType());

        // 绘制标记
        drawMarkerShape(canvas, position, marker);
    }

    /**
     * 获取标记颜色
     */
    private int getMarkerColor(MarkerData marker) {
        if (marker.getColor() != 0) {
            return marker.getColor();
        }

        switch (marker.getType()) {
            case BUY:
                return config.getBuyColor();
            case SELL:
                return config.getSellColor();
            case NUMBER:
                return config.getNumberColor();
            case UP_TRIANGLE:
                return config.getUpTriangleColor();
            case DOWN_TRIANGLE:
                return config.getDownTriangleColor();
            default:
                return config.getBuyColor();
        }
    }

    /**
     * 计算标记位置
     */
    private MarkerPosition calculateMarkerPosition(T klineEntry, MarkerData marker, int index,
                                                   float safeTopY, float safeBottomY,
                                                   float safeLeftX, float safeRightX) {

        float xValue = dataAdapter.getXValue(klineEntry);
        float high = dataAdapter.getHigh(klineEntry);
        float low = dataAdapter.getLow(klineEntry);

        // 转换为屏幕坐标
        float screenX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(xValue, 0).x;
        screenX = Math.max(safeLeftX, Math.min(screenX, safeRightX));

        float markerSizePx = config.getMarkerSize() * density;
        float markerScreenY;
        float lineStartY;
        boolean isMarkerOnTop;

        switch (marker.getType()) {
            case BUY:
                // 买入标记在低点下方
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, low).y;
                markerScreenY = lineStartY + markerSizePx * config.getMarkerOffsetMultiplier();
                markerScreenY = Math.min(markerScreenY, safeBottomY);
                isMarkerOnTop = false;
                break;

            case SELL:
                // 卖出标记在高点上方
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, high).y;
                markerScreenY = lineStartY - markerSizePx * config.getMarkerOffsetMultiplier();
                markerScreenY = Math.max(markerScreenY, safeTopY);
                isMarkerOnTop = true;
                break;

            case UP_TRIANGLE:
                // 上三角标记在高点上方
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, high).y;
                markerScreenY = lineStartY - markerSizePx * config.getTriangleOffsetMultiplier();
                markerScreenY = Math.max(markerScreenY, safeTopY);
                isMarkerOnTop = true;
                break;

            case DOWN_TRIANGLE:
                // 下三角标记在低点下方
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, low).y;
                markerScreenY = lineStartY + markerSizePx * config.getTriangleOffsetMultiplier();
                markerScreenY = Math.min(markerScreenY, safeBottomY);
                isMarkerOnTop = false;
                break;

            case NUMBER:
            default:
                // 数字标记根据奇偶性错开显示
                float midY = (high + low) / 2;
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, midY).y;

                if (index % 2 == 0) {
                    markerScreenY = lineStartY - markerSizePx;
                    isMarkerOnTop = true;
                } else {
                    markerScreenY = lineStartY + markerSizePx;
                    isMarkerOnTop = false;
                }
                markerScreenY = Math.max(safeTopY, Math.min(markerScreenY, safeBottomY));
                break;
        }

        return new MarkerPosition(screenX, markerScreenY, lineStartY, isMarkerOnTop);
    }

    /**
     * 绘制连接线
     */
    private void drawConnectionLine(Canvas canvas, MarkerPosition position, MarkerData.MarkerType type) {
        float lineLength;

        if (type == MarkerData.MarkerType.NUMBER ||
                type == MarkerData.MarkerType.UP_TRIANGLE ||
                type == MarkerData.MarkerType.DOWN_TRIANGLE) {
            lineLength = config.getShortLineLength() * density;
        } else {
            lineLength = config.getFixedLineLength() * density;
        }

        float lineEndY;
        if (position.isMarkerOnTop) {
            lineEndY = position.lineStartY - lineLength;
        } else {
            lineEndY = position.lineStartY + lineLength;
        }

        canvas.drawLine(position.screenX, position.lineStartY, position.screenX, lineEndY, dashLinePaint);
    }

    /**
     * 绘制标记形状
     */
    private void drawMarkerShape(Canvas canvas, MarkerPosition position, MarkerData marker) {
        float markerSizePx = config.getMarkerSize() * density;

        switch (marker.getType()) {
            case UP_TRIANGLE:
            case DOWN_TRIANGLE:
                drawTriangleMarker(canvas, position.screenX, position.markerScreenY, marker.getType());
                break;

            case NUMBER:
                drawNumberMarker(canvas, position, marker.getText());
                break;

            default:
                drawRectangleMarker(canvas, position.screenX, position.markerScreenY, marker.getText());
                break;
        }
    }

    /**
     * 绘制三角形标记
     */
    private void drawTriangleMarker(Canvas canvas, float centerX, float centerY, MarkerData.MarkerType type) {
        Path trianglePath = new Path();
        float triangleSize = config.getMarkerSize() * density * 0.8f;

        if (type == MarkerData.MarkerType.UP_TRIANGLE) {
            trianglePath.moveTo(centerX, centerY - triangleSize / 2);
            trianglePath.lineTo(centerX - triangleSize / 2, centerY + triangleSize / 2);
            trianglePath.lineTo(centerX + triangleSize / 2, centerY + triangleSize / 2);
            trianglePath.close();
        } else if (type == MarkerData.MarkerType.DOWN_TRIANGLE) {
            trianglePath.moveTo(centerX, centerY + triangleSize / 2);
            trianglePath.lineTo(centerX - triangleSize / 2, centerY - triangleSize / 2);
            trianglePath.lineTo(centerX + triangleSize / 2, centerY - triangleSize / 2);
            trianglePath.close();
        }

        canvas.drawPath(trianglePath, markerPaint);
    }

    /**
     * 绘制数字标记
     */
    private void drawNumberMarker(Canvas canvas, MarkerPosition position, String text) {
        float markerSizePx = config.getMarkerSize() * density;
        float shortLineLength = config.getShortLineLength() * density;

        // 计算引出线
        float lineEndX = position.screenX + markerSizePx * 1.5f;
        float lineEndY;

        if (position.isMarkerOnTop) {
            lineEndY = position.markerScreenY - markerSizePx * 0.8f;
        } else {
            lineEndY = position.markerScreenY + markerSizePx * 0.8f;
        }

        // 绘制引出线
        canvas.drawLine(position.screenX, position.lineStartY + (position.isMarkerOnTop ? -shortLineLength : shortLineLength),
                lineEndX, lineEndY, linePaint);

        // 绘制文字
        float textX = lineEndX + markerSizePx * 0.3f;
        float textY = lineEndY + numberTextPaint.getTextSize() / 3;
        canvas.drawText(text, textX, textY, numberTextPaint);
    }

    /**
     * 绘制矩形标记
     */
    private void drawRectangleMarker(Canvas canvas, float centerX, float centerY, String text) {
        float markerSizePx = config.getMarkerSize() * density;
        float paddingPx = config.getPadding() * density;

        float left = centerX - markerSizePx / 2;
        float top = centerY - markerSizePx / 2;
        float right = centerX + markerSizePx / 2;
        float bottom = centerY + markerSizePx / 2;

        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rectF, paddingPx, paddingPx, markerPaint);

        // 绘制文字
        float textY = centerY + (textPaint.getTextSize() / 3);
        canvas.drawText(text, centerX, textY, textPaint);
    }

    // IMarker接口实现（MPAndroidChart要求）
    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, 0);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return getOffset();
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // 不需要刷新内容
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // 不使用这个方法，使用自定义的drawMarkers方法
    }

    /**
     * 标记位置信息类
     */
    private static class MarkerPosition {
        final float screenX;
        final float markerScreenY;
        final float lineStartY;
        final boolean isMarkerOnTop;

        MarkerPosition(float screenX, float markerScreenY, float lineStartY, boolean isMarkerOnTop) {
            this.screenX = screenX;
            this.markerScreenY = markerScreenY;
            this.lineStartY = lineStartY;
            this.isMarkerOnTop = isMarkerOnTop;
        }
    }
} 