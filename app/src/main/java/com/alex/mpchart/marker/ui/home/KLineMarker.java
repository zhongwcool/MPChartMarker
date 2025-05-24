package com.alex.mpchart.marker.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class KLineMarker implements IMarker {
    private final Paint markerPaint;
    private final Paint textPaint;
    private final Paint dashLinePaint;  // 虚线画笔
    private final float markerSize;
    private final float padding;
    private final CombinedChart chart;
    private final List<KLineEntry> entries;
    private final float fixedLineLength; // 固定虚线长度
    private final float shortLineLength; // 数字标记的短虚线长度

    public KLineMarker(Context context, CombinedChart chart, List<KLineEntry> entries) {
        this.chart = chart;
        this.entries = entries;

        float density = context.getResources().getDisplayMetrics().density;
        this.markerSize = 12 * density; // 标记大小，减小到12dp
        this.padding = 3 * density; // 内边距，减小到3dp
        this.fixedLineLength = 30 * density; // 固定虚线长度为30dp
        this.shortLineLength = 5 * density; // 数字标记的短虚线长度为5dp

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(9 * density); // 文字大小，减小到9sp
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 初始化虚线画笔
        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setStyle(Paint.Style.STROKE);
        dashLinePaint.setStrokeWidth(1 * density); // 1dp宽度
        dashLinePaint.setPathEffect(new DashPathEffect(new float[]{3 * density, 3 * density}, 0)); // 3dp虚线，3dp间隔
    }

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
        // 不需要刷新内容，因为我们自定义绘制方法
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // 这个方法在highlight时会被调用，我们不需要
    }

    // 自定义方法：绘制所有标记
    public void drawMarkers(Canvas canvas) {
        Log.d("KLineMarker", "drawMarkers called, entries size: " + entries.size());

        // 获取图表可见区域的时间范围
        float minTime = chart.getLowestVisibleX();
        float maxTime = chart.getHighestVisibleX();

        Log.d("KLineMarker", "Visible time range: " + minTime + " to " + maxTime);

        // 计算有多少个标记需要绘制
        int markerCount = 0;
        for (int i = 0; i < entries.size(); i++) {
            KLineEntry entry = entries.get(i);
            // 检查该条目是否在可见时间范围内
            if (entry.getXValue() >= minTime && entry.getXValue() <= maxTime && entry.hasMarker) {
                markerCount++;
                Log.d("KLineMarker", "Found marker at index " + i + ", type: " + entry.markerType + ", text: " + entry.markerText);
            }
        }

        Log.d("KLineMarker", "Total markers to draw: " + markerCount);

        // 获取图表顶部和底部Y轴值对应的屏幕坐标
        float topY = chart.getAxisLeft().getAxisMaximum();
        float bottomY = chart.getAxisLeft().getAxisMinimum();
        float screenTopY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(0, topY).y;
        float screenBottomY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(0, bottomY).y;

        // 获取图表的左右边界
        float leftBoundary = chart.getViewPortHandler().contentLeft();
        float rightBoundary = chart.getViewPortHandler().contentRight();

        // 计算安全可视区域（留出一定边距）
        float safeTopY = screenTopY + markerSize * 1.5f;
        float safeBottomY = screenBottomY - markerSize * 1.5f;
        float safeLeftX = leftBoundary + markerSize;
        float safeRightX = rightBoundary - markerSize;

        for (int i = 0; i < entries.size(); i++) {
            KLineEntry entry = entries.get(i);
            // 检查该条目是否在可见时间范围内且有标记
            if (entry.getXValue() >= minTime && entry.getXValue() <= maxTime && entry.hasMarker) {
                // 获取蜡烛图上的坐标
                float x = entry.getXValue(); // x坐标使用相对天数

                // 设置标记颜色
                switch (entry.markerType) {
                    case BUY:
                        markerPaint.setColor(Color.parseColor("#4CAF50")); // 绿色
                        dashLinePaint.setColor(Color.parseColor("#4CAF50")); // 虚线也用相同颜色
                        break;
                    case SELL:
                        markerPaint.setColor(Color.parseColor("#F44336")); // 红色
                        dashLinePaint.setColor(Color.parseColor("#F44336")); // 虚线也用相同颜色
                        break;
                    case NUMBER:
                        markerPaint.setColor(Color.parseColor("#80BDBDBD")); // 半透明浅灰色
                        dashLinePaint.setColor(Color.parseColor("#BDBDBD")); // 虚线保持不透明
                        break;
                    case UP_TRIANGLE:
                        markerPaint.setColor(Color.parseColor("#FF5722")); // 橙红色，表示激增
                        dashLinePaint.setColor(Color.parseColor("#FF5722"));
                        break;
                    case DOWN_TRIANGLE:
                        markerPaint.setColor(Color.parseColor("#9C27B0")); // 紫色，表示陡降
                        dashLinePaint.setColor(Color.parseColor("#9C27B0"));
                        break;
                    default:
                        continue; // 没有标记类型，跳过
                }

                // 确定标记位置（标记方块错开显示）
                float markerScreenY;

                // 根据标记类型设置不同的垂直偏移，使标记错开显示
                boolean isMarkerOnTop = false; // 标记是否在上方

                switch (entry.markerType) {
                    case BUY:
                        // 买入标记在低点下方小距离处，不要太远
                        float lowY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, entry.low).y;
                        markerScreenY = lowY + markerSize * 2.0f; // 只偏移2个标记大小

                        // 确保不超出安全范围
                        markerScreenY = Math.min(markerScreenY, safeBottomY);
                        isMarkerOnTop = false;
                        break;
                    case SELL:
                        // 卖出标记在高点上方小距离处，不要太远
                        float highY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, entry.high).y;
                        markerScreenY = highY - markerSize * 2.0f; // 只偏移2个标记大小

                        // 确保不超出安全范围
                        markerScreenY = Math.max(markerScreenY, safeTopY);
                        isMarkerOnTop = true;
                        break;
                    case NUMBER:
                        // 数字标记直接放在K线上，根据奇偶性放在不同位置避免重叠
                        float midY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, (entry.high + entry.low) / 2).y;

                        if (i % 2 == 0) {
                            // 偶数放在中上位置
                            float upperMidY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                    .getPixelForValues(x, (entry.high + (entry.high + entry.low) / 2) / 2).y;
                            markerScreenY = upperMidY;
                            isMarkerOnTop = true;
                        } else {
                            // 奇数放在中下位置
                            float lowerMidY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                    .getPixelForValues(x, (entry.low + (entry.high + entry.low) / 2) / 2).y;
                            markerScreenY = lowerMidY;
                            isMarkerOnTop = false;
                        }

                        // 确保在安全范围内
                        markerScreenY = Math.max(safeTopY, Math.min(markerScreenY, safeBottomY));
                        break;
                    case UP_TRIANGLE:
                        // 上三角标记在高点上方
                        float upTriangleHighY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, entry.high).y;
                        markerScreenY = upTriangleHighY - markerSize * 1.5f;
                        markerScreenY = Math.max(markerScreenY, safeTopY);
                        isMarkerOnTop = true;
                        break;
                    case DOWN_TRIANGLE:
                        // 下三角标记在低点下方
                        float downTriangleLowY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, entry.low).y;
                        markerScreenY = downTriangleLowY + markerSize * 1.5f;
                        markerScreenY = Math.min(markerScreenY, safeBottomY);
                        isMarkerOnTop = false;
                        break;
                    default:
                        markerScreenY = screenTopY;
                        isMarkerOnTop = true;
                        break;
                }

                // 根据标记位置确定虚线起点
                float candleY;
                if (isMarkerOnTop) {
                    // 标记在上方，虚线起点是K线的最高点
                    candleY = entry.high;
                } else {
                    // 标记在下方，虚线起点是K线的最低点
                    candleY = entry.low;
                }

                // 转换为屏幕坐标
                float screenX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(x, candleY).x;
                float lineStartY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(x, candleY).y;

                // 检查并调整X坐标，确保标记不会超出左右边界
                screenX = Math.max(safeLeftX, Math.min(screenX, safeRightX));

                // 处理数字标记和三角形标记的特殊位置（都使用短虚线）
                if (entry.markerType == KLineEntry.MarkerType.NUMBER ||
                        entry.markerType == KLineEntry.MarkerType.UP_TRIANGLE ||
                        entry.markerType == KLineEntry.MarkerType.DOWN_TRIANGLE) {

                    if (entry.markerType == KLineEntry.MarkerType.NUMBER) {
                        // 更新数字标记的X坐标，使其直接位于K线上方或下方
                        float originalScreenX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                                .getPixelForValues(x, (entry.high + entry.low) / 2).x;
                        screenX = Math.max(safeLeftX, Math.min(originalScreenX, safeRightX));
                    }

                    // 为数字标记和三角形标记绘制短虚线
                    float lineEndY;
                    if (isMarkerOnTop) {
                        lineEndY = lineStartY - shortLineLength;
                    } else {
                        lineEndY = lineStartY + shortLineLength;
                    }
                    canvas.drawLine(screenX, lineStartY, screenX, lineEndY, dashLinePaint);

                    // 根据虚线终点调整标记位置
                    if (isMarkerOnTop) {
                        markerScreenY = lineEndY - markerSize / 2;
                    } else {
                        markerScreenY = lineEndY + markerSize / 2;
                    }
                } else {
                    // 对于买入卖出点，计算虚线终点
                    float lineEndY;

                    // 根据标记位置确定虚线方向
                    if (isMarkerOnTop) {
                        // 标记在K线上方
                        lineEndY = lineStartY - fixedLineLength;
                        // 确保虚线不会太短
                        if (markerScreenY > lineEndY) {
                            markerScreenY = lineEndY - markerSize;
                        }
                    } else {
                        // 标记在K线下方
                        lineEndY = lineStartY + fixedLineLength;
                        // 确保虚线不会太短
                        if (markerScreenY < lineEndY) {
                            markerScreenY = lineEndY + markerSize;
                        }
                    }

                    // 对于买入卖出点，如果线很短，调整为较短的固定长度
                    if (entry.markerType == KLineEntry.MarkerType.BUY ||
                            entry.markerType == KLineEntry.MarkerType.SELL) {
                        float minLineLength = fixedLineLength * 0.3f; // 至少30%的长度

                        if (isMarkerOnTop) {
                            if (Math.abs(lineStartY - markerScreenY) < minLineLength) {
                                markerScreenY = lineStartY - minLineLength - markerSize;
                            }
                        } else {
                            if (Math.abs(markerScreenY - lineStartY) < minLineLength) {
                                markerScreenY = lineStartY + minLineLength + markerSize;
                            }
                        }
                    }

                    // 绘制连接虚线（垂直方向）
                    canvas.drawLine(screenX, lineStartY, screenX, isMarkerOnTop ? markerScreenY + markerSize / 2 : markerScreenY - markerSize / 2, dashLinePaint);
                }

                // 最终检查标记位置，确保完全在边界内
                markerScreenY = Math.max(safeTopY, Math.min(markerScreenY, safeBottomY));

                // 根据标记类型选择绘制方式
                if (entry.markerType == KLineEntry.MarkerType.UP_TRIANGLE ||
                        entry.markerType == KLineEntry.MarkerType.DOWN_TRIANGLE) {
                    // 绘制三角形标记
                    drawTriangleMarker(canvas, screenX, markerScreenY, entry.markerType);
                } else if (entry.markerType == KLineEntry.MarkerType.NUMBER) {
                    // 绘制数字标记：引出线 + 文字
                    drawNumberMarker(canvas, screenX, markerScreenY, entry.markerText, isMarkerOnTop);
                } else {
                    // 绘制普通方块标记（买入、卖出）
                    float left = screenX - markerSize / 2;
                    float top = markerScreenY - markerSize / 2;
                    float right = screenX + markerSize / 2;
                    float bottom = markerScreenY + markerSize / 2;

                    RectF rectF = new RectF(left, top, right, bottom);
                    canvas.drawRoundRect(rectF, padding, padding, markerPaint);

                    // 其他标记保持白色文字
                    textPaint.setColor(Color.WHITE);

                    // 绘制标记文本
                    float textX = screenX;
                    float textY = markerScreenY + (textPaint.getTextSize() / 3); // 文本垂直居中

                    canvas.drawText(entry.markerText, textX, textY, textPaint);
                }
            }
        }
    }

    // 绘制三角形标记的方法
    private void drawTriangleMarker(Canvas canvas, float centerX, float centerY, KLineEntry.MarkerType type) {
        Path trianglePath = new Path();
        float triangleSize = markerSize * 0.8f; // 三角形稍小一些

        if (type == KLineEntry.MarkerType.UP_TRIANGLE) {
            // 绘制向上的三角形
            trianglePath.moveTo(centerX, centerY - triangleSize / 2); // 顶点
            trianglePath.lineTo(centerX - triangleSize / 2, centerY + triangleSize / 2); // 左下角
            trianglePath.lineTo(centerX + triangleSize / 2, centerY + triangleSize / 2); // 右下角
            trianglePath.close();
        } else if (type == KLineEntry.MarkerType.DOWN_TRIANGLE) {
            // 绘制向下的三角形
            trianglePath.moveTo(centerX, centerY + triangleSize / 2); // 底点
            trianglePath.lineTo(centerX - triangleSize / 2, centerY - triangleSize / 2); // 左上角
            trianglePath.lineTo(centerX + triangleSize / 2, centerY - triangleSize / 2); // 右上角
            trianglePath.close();
        }

        canvas.drawPath(trianglePath, markerPaint);
    }

    // 绘制数字标记的方法
    private void drawNumberMarker(Canvas canvas, float centerX, float centerY, String text, boolean isMarkerOnTop) {
        // 计算引出线的起点（K线上的点）
        float lineStartX = centerX;
        float lineStartY;

        // 计算引出线的终点（数字显示位置）
        float lineEndX = centerX + markerSize * 1.5f; // 向右偏移1.5个标记大小
        float lineEndY;

        if (isMarkerOnTop) {
            // 标记在上方，引出线从K线高点向上右方向延伸
            lineStartY = centerY + shortLineLength; // 从短虚线的终点开始
            lineEndY = centerY - markerSize * 0.8f; // 向上延伸
        } else {
            // 标记在下方，引出线从K线低点向下右方向延伸
            lineStartY = centerY - shortLineLength; // 从短虚线的终点开始
            lineEndY = centerY + markerSize * 0.8f; // 向下延伸
        }

        // 创建引出线的画笔
        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#666666")); // 灰色引出线
        linePaint.setStrokeWidth(1.5f);
        linePaint.setAntiAlias(true);

        // 绘制引出线（从K线指向数字位置的斜线）
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, linePaint);

        // 设置文字样式
        Paint numberTextPaint = new Paint();
        numberTextPaint.setColor(Color.parseColor("#333333")); // 深灰色文字
        numberTextPaint.setTextSize(textPaint.getTextSize()); // 与其他标记文字大小一致
        numberTextPaint.setAntiAlias(true);
        numberTextPaint.setTypeface(Typeface.DEFAULT_BOLD); // 粗体文字
        numberTextPaint.setTextAlign(Paint.Align.LEFT); // 左对齐，因为文字在线的右侧

        // 计算文字位置（在引出线终点的右侧）
        float textX = lineEndX + markerSize * 0.3f; // 在线终点右侧留一点间距
        float textY = lineEndY + numberTextPaint.getTextSize() / 3; // 文本垂直居中

        // 绘制文字
        canvas.drawText(text, textX, textY, numberTextPaint);
    }
} 