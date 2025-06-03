package com.alex.klinemarker.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;
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
    private static final boolean DEBUG = false; // 生产版本关闭DEBUG

    private final Context context;
    private final CombinedChart chart;
    private final KLineDataAdapter<T> dataAdapter;
    private final MarkerRendererFactory rendererFactory;

    // 绘制相关的Paint对象
    private Paint dashLinePaint;
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
                               KLineDataAdapter<T> dataAdapter) {
        this.context = context;
        this.chart = chart;
        this.dataAdapter = dataAdapter;
        this.density = context.getResources().getDisplayMetrics().density;
        this.dateToMarkerMap = new HashMap<>();
        this.rendererFactory = new MarkerRendererFactory(context);

        // 初始化Paint对象
        initPaints();
    }

    /**
     * 初始化绘制相关的Paint对象
     */
    private void initPaints() {
        // 虚线画笔
        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setStyle(Paint.Style.STROKE);
        dashLinePaint.setStrokeWidth(density);
        dashLinePaint.setPathEffect(new DashPathEffect(
                new float[]{5f * density, 3f * density}, 0));

        // 引出线画笔
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xFF666666); // 灰色引出线
        linePaint.setStrokeWidth(1.5f * density);
    }

    /**
     * 设置K线数据
     */
    public void setKLineData(List<T> klineData) {
        this.klineData = klineData;
    }

    /**
     * 设置标记数据
     */
    public void setMarkers(List<MarkerData> markers) {
        this.markers = markers;
        updateDateToMarkerMap();
    }

    /**
     * 更新日期到标记的映射关系
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

        // 计算安全可视区域 - 标记应该真正贴着顶部/底部显示
        float defaultMarkerSize = 24f;
        float markerSizePx = defaultMarkerSize * density;
        // 标记中心点应该距离边界半个标记大小，这样标记边缘就贴着边界了
        float safeTopY = contentTop + markerSizePx * 0.5f;
        float safeBottomY = contentBottom - markerSizePx * 0.5f; 
        float safeLeftX = leftBoundary + markerSizePx;
        float safeRightX = rightBoundary - markerSizePx;

        if (DEBUG) {
            Log.d(TAG, String.format("边界信息: contentTop=%.1f, contentBottom=%.1f, markerSize=%.1f",
                    contentTop, contentBottom, markerSizePx));
            Log.d(TAG, String.format("安全区域: safeTopY=%.1f, safeBottomY=%.1f", safeTopY, safeBottomY));
        }

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

            if (marker != null) {
                drawSingleMarker(canvas, klineEntry, marker, i, safeTopY, safeBottomY, safeLeftX, safeRightX);
            }
        }
    }

    /**
     * 绘制单个标记
     */
    private void drawSingleMarker(Canvas canvas, T klineEntry, MarkerData marker, int index,
                                  float safeTopY, float safeBottomY, float safeLeftX, float safeRightX) {

        // 获取对应的渲染器
        IMarkerRenderer renderer = rendererFactory.getRenderer(marker.getConfig().getShape());
        if (renderer == null) {
            Log.w(TAG, "No renderer found for shape: " + marker.getConfig().getShape());
            return;
        }

        // 计算标记位置
        MarkerRenderPosition position = calculateMarkerPosition(klineEntry, marker, index,
                safeTopY, safeBottomY, safeLeftX, safeRightX);

        // 绘制连接线
        drawConnectionLine(canvas, position, marker);

        // 对于纯文字标记，需要计算引出线末端位置来放置文字
        if (marker.getConfig().getShape() == MarkerShape.NONE) {
            if (position.actualLineLength > 0) {
                // 有引出线时，计算引出线末端位置
                float angleRadians = (float) Math.toRadians(30); // 30度角
                float actualDistance = Math.abs(position.markerScreenY - position.lineStartY);
                float deltaX = actualDistance * (float) Math.cos(angleRadians);

                // 引出线末端X坐标
                float endX = position.screenX + deltaX;

                // 使用引出线末端位置绘制文字
                renderer.drawMarker(canvas, endX, position.markerScreenY, marker, context);
            } else {
                // 没有引出线时，直接在标记位置绘制文字
                renderer.drawMarker(canvas, position.screenX, position.markerScreenY, marker, context);
            }
        } else {
            // 其他类型的标记直接在计算的位置绘制
            renderer.drawMarker(canvas, position.screenX, position.markerScreenY, marker, context);
        }
    }

    /**
     * 计算标记位置
     */
    private MarkerRenderPosition calculateMarkerPosition(T klineEntry, MarkerData marker, int index,
                                                         float safeTopY, float safeBottomY, float safeLeftX, float safeRightX) {
        float xValue = dataAdapter.getXValue(klineEntry);
        float high = dataAdapter.getHigh(klineEntry);
        float low = dataAdapter.getLow(klineEntry);

        // 计算屏幕X坐标
        float screenX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(xValue, 0).x;

        // 获取引线长度配置
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(
                context, marker.getConfig().getLineLength());
        float actualLineLength = originalLineLength;

        float lineStartY;
        float markerScreenY;
        boolean isMarkerOnTop;

        // 根据标记位置配置决定位置
        switch (marker.getConfig().getPosition()) {
            case BELOW:
                // 标记在下方，虚线起始点为K线最低价点
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, low).y;
                markerScreenY = lineStartY + originalLineLength;

                // 检查是否超出底部边界
                if (markerScreenY > safeBottomY) {
                    markerScreenY = safeBottomY;
                    // 动态调整虚线长度
                    actualLineLength = Math.max(0, markerScreenY - lineStartY);
                }
                isMarkerOnTop = false;
                break;

            case ABOVE:
                // 标记在上方，虚线起始点为K线最高价点
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, high).y;
                markerScreenY = lineStartY - originalLineLength;

                // 检查是否超出顶部边界
                if (markerScreenY < safeTopY) {
                    markerScreenY = safeTopY;
                    // 动态调整虚线长度
                    actualLineLength = Math.max(0, lineStartY - markerScreenY);
                }
                isMarkerOnTop = true;
                break;

            default: // AUTO
                // 自动选择：根据奇偶性错开显示
                if (index % 2 == 0) {
                    // 偶数索引：标记在上方
                    lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                            .getPixelForValues(xValue, high).y;
                    markerScreenY = lineStartY - originalLineLength;

                    // 检查是否超出顶部边界
                    if (markerScreenY < safeTopY) {
                        markerScreenY = safeTopY;
                        actualLineLength = Math.max(0, lineStartY - markerScreenY);
                    }
                    isMarkerOnTop = true;
                } else {
                    // 奇数索引：标记在下方
                    lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                            .getPixelForValues(xValue, low).y;
                    markerScreenY = lineStartY + originalLineLength;

                    // 检查是否超出底部边界
                    if (markerScreenY > safeBottomY) {
                        markerScreenY = safeBottomY;
                        actualLineLength = Math.max(0, markerScreenY - lineStartY);
                    }
                    isMarkerOnTop = false;
                }
                break;
        }

        return new MarkerRenderPosition(screenX, markerScreenY, lineStartY, isMarkerOnTop, actualLineLength);
    }

    /**
     * 绘制连接线
     */
    private void drawConnectionLine(Canvas canvas, MarkerRenderPosition position, MarkerData marker) {
        // 如果不显示连接线，则不绘制连接线
        if (!marker.getConfig().isShowLine()) {
            return;
        }

        // 检查LineLength是否允许绘制连接线（LineLength.NONE不绘制连接线）
        if (!com.alex.klinemarker.utils.LineLengthUtils.shouldDrawLine(marker.getConfig().getLineLength())) {
            return;
        }

        // 设置连接线颜色
        int lineColor = marker.getConfig().getLineColor();
        dashLinePaint.setColor(lineColor);

        // 对于纯文字标记，绘制斜实线连接
        if (marker.getConfig().getShape() == MarkerShape.NONE) {
            drawTextOnlyConnectionLine(canvas, position, marker);
            return;
        }

        // 检查虚线长度是否被压缩
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(
                context, marker.getConfig().getLineLength());
        boolean isCompressed = Math.abs(position.actualLineLength - originalLineLength) > 1f; // 允许1像素的误差

        if (isCompressed && position.actualLineLength > 0) {
            // 虚线被压缩时，使用更粗的线条以增强视觉效果
            Paint compressedLinePaint = new Paint(dashLinePaint);
            compressedLinePaint.setStrokeWidth(dashLinePaint.getStrokeWidth() * 1.5f);
            compressedLinePaint.setAlpha(200); // 稍微降低透明度
            canvas.drawLine(position.screenX, position.lineStartY, position.screenX, position.markerScreenY, compressedLinePaint);
        } else if (position.actualLineLength > 0) {
            // 根据配置选择虚线或实线
            if (marker.getConfig().isDashedLine()) {
                canvas.drawLine(position.screenX, position.lineStartY, position.screenX, position.markerScreenY, dashLinePaint);
            } else {
                canvas.drawLine(position.screenX, position.lineStartY, position.screenX, position.markerScreenY, linePaint);
            }
        }
        // 如果actualLineLength为0，则不绘制连接线（标记直接贴在K线上）
    }

    /**
     * 为纯文字标记绘制斜线连接
     */
    private void drawTextOnlyConnectionLine(Canvas canvas, MarkerRenderPosition position, MarkerData marker) {
        // 如果实际虚线长度为0，不绘制连接线
        if (position.actualLineLength <= 0) {
            return;
        }

        // 检查LineLength是否允许绘制连接线（LineLength.NONE不绘制连接线）
        if (!com.alex.klinemarker.utils.LineLengthUtils.shouldDrawLine(marker.getConfig().getLineLength())) {
            return;
        }

        // 使用更细的实线画笔
        Paint solidLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidLinePaint.setStyle(Paint.Style.STROKE);
        solidLinePaint.setStrokeWidth(density);
        solidLinePaint.setColor(marker.getConfig().getLineColor());

        // 检查是否被压缩
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(
                context, marker.getConfig().getLineLength());
        boolean isCompressed = Math.abs(position.actualLineLength - originalLineLength) > 1f;

        if (isCompressed) {
            solidLinePaint.setStrokeWidth(1.3f * density);
            solidLinePaint.setAlpha(200);
        }

        // 计算斜线的角度和长度 - 基于实际虚线长度
        float angleRadians = (float) Math.toRadians(30); // 30度角

        // 使用实际的标记位置距离来计算斜线
        float actualDistance = Math.abs(position.markerScreenY - position.lineStartY);
        float deltaX = actualDistance * (float) Math.cos(angleRadians);

        // 根据标记位置决定斜线方向
        float endX = position.screenX + deltaX;
        float endY = position.markerScreenY;

        // 绘制从K线位置到文字位置的斜实线
        canvas.drawLine(position.screenX, position.lineStartY, endX, endY, solidLinePaint);
    }

    /**
     * 注册自定义渲染器
     */
    public void registerRenderer(MarkerShape shape, IMarkerRenderer renderer) {
        rendererFactory.registerRenderer(shape, renderer);
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
     * 标记渲染位置信息类
     */
    private static class MarkerRenderPosition {
        final float screenX;
        final float markerScreenY;
        final float lineStartY;
        final boolean isMarkerOnTop;
        final float actualLineLength;

        MarkerRenderPosition(float screenX, float markerScreenY, float lineStartY, boolean isMarkerOnTop, float actualLineLength) {
            this.screenX = screenX;
            this.markerScreenY = markerScreenY;
            this.lineStartY = lineStartY;
            this.isMarkerOnTop = isMarkerOnTop;
            this.actualLineLength = actualLineLength;
        }
    }
} 