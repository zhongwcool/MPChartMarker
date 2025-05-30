package com.alex.klinemarker.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;
import com.alex.klinemarker.data.MarkerType;
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
    private final MarkerConfig config;
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
                               KLineDataAdapter<T> dataAdapter, MarkerConfig config) {
        this.context = context;
        this.chart = chart;
        this.dataAdapter = dataAdapter;
        this.config = config != null ? config : new MarkerConfig();
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
        dashLinePaint.setStrokeWidth(config.getLineWidth() * density);
        dashLinePaint.setPathEffect(new DashPathEffect(
                new float[]{config.getDashLength() * density, config.getDashGap() * density}, 0));

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
        float markerSizePx = config.getMarkerSize() * density;
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

            if (marker != null && marker.getType() != MarkerType.NONE) {
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
        IMarkerRenderer renderer = rendererFactory.getRenderer(marker.getStyle());
        if (renderer == null) {
            Log.w(TAG, "No renderer found for style: " + marker.getStyle());
            return;
        }

        // 设置连接线颜色
        int markerColor = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker);
        dashLinePaint.setColor(markerColor);

        // 计算标记位置
        MarkerPosition position = calculateMarkerPosition(klineEntry, marker, index,
                safeTopY, safeBottomY, safeLeftX, safeRightX);

        // 绘制连接线
        drawConnectionLine(canvas, position, marker);

        // 对于TEXT_ONLY标记，需要计算引出线末端位置来放置文字
        if (marker.getStyle() == MarkerStyle.TEXT_ONLY) {
            if (position.actualLineLength > 0) {
                // 有引出线时，计算引出线末端位置
                float angleRadians = (float) Math.toRadians(30); // 30度角
                float actualDistance = Math.abs(position.markerScreenY - position.lineStartY);
                float deltaX = actualDistance * (float) Math.cos(angleRadians);

                // 引出线末端X坐标
                float lineEndX = position.screenX + deltaX;

                // 使用引出线末端位置作为文字的起始位置
                renderer.drawMarker(canvas, lineEndX, position.markerScreenY, marker, config, context);
            } else {
                // 虚线长度为0时，文字直接显示在K线旁边
                float textStartX = position.screenX + config.getMarkerSize() * density * 0.3f; // 稍微偏移避免与K线重叠
                renderer.drawMarker(canvas, textStartX, position.markerScreenY, marker, config, context);
            }
        } else {
            // 其他标记使用正常的中心位置
            renderer.drawMarker(canvas, position.screenX, position.markerScreenY, marker, config, context);
        }
    }

    /**
     * 获取标记默认颜色
     */
    private int getDefaultColor(MarkerData marker) {
        switch (marker.getType()) {
            case BUY:
                return config.getBuyColor();
            case SELL:
                return config.getSellColor();
            case SURGE:
                return config.getUpTriangleColor();
            case PLUNGE:
                return config.getDownTriangleColor();
            case EVENT:
            case INFO:
                return config.getNumberColor();
            case WARNING:
                return 0xFFFF9800; // 橙色
            case STOP_LOSS:
                return 0xFFE91E63; // 粉红色
            case TAKE_PROFIT:
                return 0xFF4CAF50; // 绿色
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

        // 获取原始虚线长度（标记与K线的距离）
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(context, marker.getLineLength());
        
        float markerScreenY;
        float lineStartY;
        boolean isMarkerOnTop;
        float actualLineLength = originalLineLength; // 实际使用的虚线长度

        if (DEBUG) {
            Log.d(TAG, String.format("处理标记: %s, 类型: %s, 原始虚线长度: %.1f",
                    marker.getText(), marker.getType(), originalLineLength));
        }

        // 根据标记类型决定位置
        switch (marker.getType()) {
            case BUY:
            case STOP_LOSS:
            case PLUNGE:
                // 买入、止损、数据骤降标记在低点下方
                // 虚线起始点：K线最低价点
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, low).y;
                markerScreenY = lineStartY + originalLineLength;

                if (DEBUG) {
                    Log.d(TAG, String.format("下方标记 - lineStartY: %.1f, 计算的markerScreenY: %.1f, safeBottomY: %.1f",
                            lineStartY, markerScreenY, safeBottomY));
                }

                // 检查是否超出底部边界
                if (markerScreenY > safeBottomY) {
                    float oldMarkerScreenY = markerScreenY;
                    markerScreenY = safeBottomY;
                    // 动态调整虚线长度
                    actualLineLength = Math.max(0, markerScreenY - lineStartY);

                    if (DEBUG) {
                        Log.d(TAG, String.format("标记被约束到底部: %.1f -> %.1f, 虚线长度: %.1f -> %.1f",
                                oldMarkerScreenY, markerScreenY, originalLineLength, actualLineLength));
                    }
                }
                isMarkerOnTop = false;
                break;

            case SELL:
            case TAKE_PROFIT:
            case SURGE:
                // 卖出、止盈、数据激增标记在高点上方
                // 虚线起始点：K线最高价点
                lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(xValue, high).y;
                markerScreenY = lineStartY - originalLineLength;

                if (DEBUG) {
                    Log.d(TAG, String.format("上方标记 - lineStartY: %.1f, 计算的markerScreenY: %.1f, safeTopY: %.1f",
                            lineStartY, markerScreenY, safeTopY));
                }

                // 检查是否超出顶部边界
                if (markerScreenY < safeTopY) {
                    float oldMarkerScreenY = markerScreenY;
                    markerScreenY = safeTopY;
                    // 动态调整虚线长度
                    actualLineLength = Math.max(0, lineStartY - markerScreenY);

                    if (DEBUG) {
                        Log.d(TAG, String.format("标记被约束到顶部: %.1f -> %.1f, 虚线长度: %.1f -> %.1f",
                                oldMarkerScreenY, markerScreenY, originalLineLength, actualLineLength));
                    }
                }
                isMarkerOnTop = true;
                break;

            default:
                // 其他标记根据奇偶性错开显示，但虚线起始点始终基于标记位置
                if (index % 2 == 0) {
                    // 偶数索引：标记在上方，虚线起始点为K线最高价点
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
                } else {
                    // 奇数索引：标记在下方，虚线起始点为K线最低价点
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
                }
                break;
        }

        return new MarkerPosition(screenX, markerScreenY, lineStartY, isMarkerOnTop, actualLineLength);
    }

    /**
     * 绘制连接线
     */
    private void drawConnectionLine(Canvas canvas, MarkerPosition position, MarkerData marker) {
        // 只有DOT样式不需要连接线（因为它通常用作简单的位置标识）
        if (marker.getStyle() == MarkerStyle.DOT) {
            return;
        }

        // 设置连接线颜色
        int markerColor = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker);
        dashLinePaint.setColor(markerColor);

        // 对于纯文字标记，绘制斜实线连接
        if (marker.getStyle() == MarkerStyle.TEXT_ONLY) {
            // 绘制从K线位置到文字位置的斜实线
            drawTextOnlyConnectionLine(canvas, position, marker);
            return;
        }

        // 检查虚线长度是否被压缩
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(context, marker.getLineLength());
        boolean isCompressed = Math.abs(position.actualLineLength - originalLineLength) > 1f; // 允许1像素的误差

        if (isCompressed && position.actualLineLength > 0) {
            // 虚线被压缩时，使用更粗的线条以增强视觉效果
            Paint compressedLinePaint = new Paint(dashLinePaint);
            compressedLinePaint.setStrokeWidth(dashLinePaint.getStrokeWidth() * 1.5f);
            compressedLinePaint.setAlpha(200); // 稍微降低透明度
            canvas.drawLine(position.screenX, position.lineStartY, position.screenX, position.markerScreenY, compressedLinePaint);

            if (DEBUG) {
                Log.d(TAG, String.format("虚线被压缩: 原始长度=%.1f, 实际长度=%.1f",
                        originalLineLength, position.actualLineLength));
            }
        } else if (position.actualLineLength > 0) {
            // 正常长度的虚线
            canvas.drawLine(position.screenX, position.lineStartY, position.screenX, position.markerScreenY, dashLinePaint);
        }
        // 如果actualLineLength为0，则不绘制连接线（标记直接贴在K线上）
    }

    /**
     * 为TEXT_ONLY标记绘制斜线连接
     */
    private void drawTextOnlyConnectionLine(Canvas canvas, MarkerPosition position, MarkerData marker) {
        // 如果实际虚线长度为0，不绘制连接线
        if (position.actualLineLength <= 0) {
            return;
        }

        // 使用更细的实线画笔
        Paint solidLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidLinePaint.setStyle(Paint.Style.STROKE);
        solidLinePaint.setStrokeWidth(density); // 改为1.0f，更细的引出线
        solidLinePaint.setColor(marker.getTextColor() != 0 ? marker.getTextColor() : config.getNumberTextColor());

        // 检查是否被压缩
        float originalLineLength = com.alex.klinemarker.utils.LineLengthUtils.getLineLengthInPixels(context, marker.getLineLength());
        boolean isCompressed = Math.abs(position.actualLineLength - originalLineLength) > 1f;

        if (isCompressed) {
            // 被压缩时稍微加粗，但仍保持相对细的效果
            solidLinePaint.setStrokeWidth(1.3f * density); // 压缩时也保持较细
            solidLinePaint.setAlpha(200);
        }

        // 计算斜线的角度和长度 - 基于实际虚线长度
        float angleRadians = (float) Math.toRadians(30); // 30度角

        // 使用实际的标记位置距离来计算斜线
        float actualDistance = Math.abs(position.markerScreenY - position.lineStartY);
        float deltaX = actualDistance * (float) Math.cos(angleRadians);

        // 根据标记位置决定斜线方向
        float endX, endY;
        if (position.isMarkerOnTop) {
            // 标记在上方，斜线向右上延伸
            endX = position.screenX + deltaX;
            endY = position.markerScreenY;
        } else {
            // 标记在下方，斜线向右下延伸
            endX = position.screenX + deltaX;
            endY = position.markerScreenY;
        }

        // 绘制从K线位置到文字位置的斜实线
        canvas.drawLine(position.screenX, position.lineStartY, endX, endY, solidLinePaint);

        if (DEBUG && isCompressed) {
            Log.d(TAG, String.format("TEXT_ONLY虚线被压缩: 原始长度=%.1f, 实际长度=%.1f",
                    originalLineLength, position.actualLineLength));
        }
    }

    /**
     * 注册自定义渲染器
     */
    public void registerRenderer(MarkerStyle style, IMarkerRenderer renderer) {
        rendererFactory.registerRenderer(style, renderer);
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
        final float actualLineLength;

        MarkerPosition(float screenX, float markerScreenY, float lineStartY, boolean isMarkerOnTop, float actualLineLength) {
            this.screenX = screenX;
            this.markerScreenY = markerScreenY;
            this.lineStartY = lineStartY;
            this.isMarkerOnTop = isMarkerOnTop;
            this.actualLineLength = actualLineLength;
        }
    }
} 