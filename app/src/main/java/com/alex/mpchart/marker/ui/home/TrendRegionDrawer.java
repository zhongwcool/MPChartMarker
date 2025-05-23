package com.alex.mpchart.marker.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.github.mikephil.charting.charts.CombinedChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendRegionDrawer {
    private static final String TAG = "TrendRegionDrawer";
    private final Paint risingRegionPaint;
    private final Paint fallingRegionPaint;
    private final CombinedChart chart;
    private final List<KLineEntry> entries;

    public TrendRegionDrawer(Context context, CombinedChart chart, List<KLineEntry> entries) {
        this.chart = chart;
        this.entries = entries;

        // 初始化上涨区间画笔（渐变绿色背景）
        risingRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        risingRegionPaint.setStyle(Paint.Style.FILL);
        risingRegionPaint.setColor(Color.parseColor("#4000C853")); // 25%透明度的绿色

        // 初始化下跌区间画笔（渐变红色背景）
        fallingRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fallingRegionPaint.setStyle(Paint.Style.FILL);
        fallingRegionPaint.setColor(Color.parseColor("#40F44336")); // 25%透明度的红色

        Log.d(TAG, "TrendRegionDrawer initialized with " + entries.size() + " entries");
    }

    public void drawTrendRegions(Canvas canvas) {
        Log.d(TAG, "drawTrendRegions called");

        // 获取图表可见区域的范围
        int minIndex = Math.max(0, (int) chart.getLowestVisibleX());
        int maxIndex = Math.min(entries.size() - 1, (int) chart.getHighestVisibleX());

        Log.d(TAG, "Visible range: " + minIndex + " to " + maxIndex);

        // 获取图表边界
        float contentTop = chart.getViewPortHandler().contentTop();
        float contentBottom = chart.getViewPortHandler().contentBottom();

        // 按区间ID分组收集趋势区间
        Map<Integer, List<KLineEntry>> trendRegions = new HashMap<>();

        for (int i = minIndex; i <= maxIndex; i++) {
            KLineEntry entry = entries.get(i);
            if (entry.isInTrendRegion && entry.regionId >= 0) {
                trendRegions.computeIfAbsent(entry.regionId, k -> new ArrayList<>()).add(entry);
                Log.d(TAG, "Found trend entry at index " + i + ", regionId=" + entry.regionId + ", type=" + entry.trendType);
            }
        }

        Log.d(TAG, "Drawing " + trendRegions.size() + " trend regions");

        // 如果没有找到趋势区间，强制绘制一些测试背景
        if (trendRegions.isEmpty()) {
            Log.d(TAG, "No trend regions found, drawing test backgrounds");
            drawTestTrendShapes(canvas, minIndex, maxIndex, contentTop, contentBottom);
        } else {
            // 绘制每个趋势区间
            for (List<KLineEntry> regionEntries : trendRegions.values()) {
                if (regionEntries.isEmpty()) continue;

                // 按索引排序确保正确的顺序
                regionEntries.sort((a, b) -> Integer.compare(a.index, b.index));

                KLineEntry.TrendType trendType = regionEntries.get(0).trendType;
                Paint regionPaint = (trendType == KLineEntry.TrendType.RISING) ?
                        risingRegionPaint : fallingRegionPaint;

                Log.d(TAG, "Drawing region with " + regionEntries.size() + " entries, type=" + trendType);
                drawTrendShadow(canvas, regionEntries, regionPaint, contentTop, contentBottom);
            }
        }
    }

    /**
     * 绘制沿K线走势的阴影背景
     */
    private void drawTrendShadow(Canvas canvas, List<KLineEntry> regionEntries,
                                 Paint paint, float contentTop, float contentBottom) {
        if (regionEntries.size() < 2) return;

        KLineEntry.TrendType trendType = regionEntries.get(0).trendType;

        // 创建路径
        Path shadowPath = new Path();

        // 获取x轴位置（图表底部）
        float xAxisY = contentBottom;

        // 获取第一个点的位置
        KLineEntry firstEntry = regionEntries.get(0);
        float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(firstEntry.index, 0).x;

        // 从x轴开始路径
        shadowPath.moveTo(startX, xAxisY);

        // 收集所有边界点
        List<Float> xPoints = new ArrayList<>();
        List<Float> yPoints = new ArrayList<>();

        // 沿着K线走势收集边界点
        for (KLineEntry entry : regionEntries) {
            float x = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(entry.index, 0).x;

            float y;
            if (trendType == KLineEntry.TrendType.RISING) {
                // 上涨趋势：沿着高点绘制，增加一些向上的偏移让效果更明显
                y = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(0, entry.high).y;
                y = y - 10; // 向上偏移10像素，让阴影效果更突出
            } else {
                // 下跌趋势：沿着低点绘制，增加一些向下的偏移
                y = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(0, entry.low).y;
                y = y + 10; // 向下偏移10像素
            }

            // 确保在图表范围内
            y = Math.max(y, contentTop);
            y = Math.min(y, contentBottom);

            xPoints.add(x);
            yPoints.add(y);
        }

        // 使用平滑曲线连接点（贝塞尔曲线近似）
        if (xPoints.size() >= 2) {
            // 移动到第一个点
            shadowPath.lineTo(xPoints.get(0), yPoints.get(0));

            if (xPoints.size() == 2) {
                // 只有两个点，直接连线
                shadowPath.lineTo(xPoints.get(1), yPoints.get(1));
            } else {
                // 多个点，使用二次贝塞尔曲线平滑连接
                for (int i = 0; i < xPoints.size() - 1; i++) {
                    float x1 = xPoints.get(i);
                    float y1 = yPoints.get(i);
                    float x2 = xPoints.get(i + 1);
                    float y2 = yPoints.get(i + 1);

                    if (i == 0) {
                        // 第一段：从起点到第一个控制点
                        float controlX = x1 + (x2 - x1) * 0.5f;
                        float controlY = y1;
                        shadowPath.quadTo(controlX, controlY, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f);
                    } else if (i == xPoints.size() - 2) {
                        // 最后一段：从当前中点到终点
                        float controlX = x1 + (x2 - x1) * 0.5f;
                        float controlY = y2;
                        shadowPath.quadTo(controlX, controlY, x2, y2);
                    } else {
                        // 中间段：平滑连接
                        float controlX = (x1 + x2) * 0.5f;
                        float controlY = (y1 + y2) * 0.5f;
                        shadowPath.quadTo(x1, y1, controlX, controlY);
                    }
                }
            }
        }

        // 获取最后一个点的位置
        KLineEntry lastEntry = regionEntries.get(regionEntries.size() - 1);
        float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(lastEntry.index, 0).x;

        // 从最后一个点回到x轴，闭合路径
        shadowPath.lineTo(endX, xAxisY);
        shadowPath.close();

        // 创建增强的渐变效果
        LinearGradient gradient;
        if (trendType == KLineEntry.TrendType.RISING) {
            // 上涨趋势：三色渐变，顶部透明，中部微绿，底部较浓绿色
            gradient = new LinearGradient(
                    0, contentTop, 0, xAxisY,
                    new int[]{
                            Color.parseColor("#0000C853"), // 完全透明的绿色
                            Color.parseColor("#2000C853"), // 12%透明度的绿色
                            Color.parseColor("#5000C853")  // 31%透明度的绿色
                    },
                    new float[]{0f, 0.3f, 1f}, // 渐变位置
                    Shader.TileMode.CLAMP
            );
        } else {
            // 下跌趋势：三色渐变，顶部较浓红色，中部微红，底部透明
            gradient = new LinearGradient(
                    0, contentTop, 0, xAxisY,
                    new int[]{
                            Color.parseColor("#50F44336"), // 31%透明度的红色
                            Color.parseColor("#20F44336"), // 12%透明度的红色
                            Color.parseColor("#00F44336")  // 完全透明的红色
                    },
                    new float[]{0f, 0.7f, 1f}, // 渐变位置
                    Shader.TileMode.CLAMP
            );
        }

        paint.setShader(gradient);

        // 绘制阴影路径
        canvas.drawPath(shadowPath, paint);

        // 清除shader
        paint.setShader(null);

        Log.d(TAG, "Drew smooth trend shadow for " + regionEntries.size() + " entries, type=" + trendType);
    }

    /**
     * 绘制测试趋势形状，用于验证绘制功能
     */
    private void drawTestTrendShapes(Canvas canvas, int minIndex, int maxIndex, float contentTop, float contentBottom) {
        float xAxisY = contentBottom;

        // 测试上涨趋势阴影（索引10-15）
        if (minIndex <= 15 && maxIndex >= 10) {
            Path testRisingPath = new Path();

            int startIdx = Math.max(10, minIndex);
            int endIdx = Math.min(15, maxIndex);

            // 从x轴开始
            float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(startIdx, 0).x;
            testRisingPath.moveTo(startX, xAxisY);

            // 创建模拟的上涨趋势线
            for (int i = startIdx; i <= endIdx; i++) {
                float x = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(i, 0).x;

                // 模拟逐渐上升的高点
                float progress = (float) (i - startIdx) / (endIdx - startIdx);
                float y = contentTop + (contentBottom - contentTop) * (0.3f - progress * 0.2f);

                testRisingPath.lineTo(x, y);
            }

            // 回到x轴闭合
            float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(endIdx, 0).x;
            testRisingPath.lineTo(endX, xAxisY);
            testRisingPath.close();

            // 设置渐变
            LinearGradient risingGradient = new LinearGradient(
                    0, contentTop, 0, xAxisY,
                    Color.parseColor("#0000C853"),
                    Color.parseColor("#6000C853"),
                    Shader.TileMode.CLAMP
            );
            risingRegionPaint.setShader(risingGradient);

            canvas.drawPath(testRisingPath, risingRegionPaint);
            risingRegionPaint.setShader(null);

            Log.d(TAG, "Drew test rising trend shadow");
        }

        // 测试下跌趋势阴影（索引25-30）
        if (minIndex <= 30 && maxIndex >= 25) {
            Path testFallingPath = new Path();

            int startIdx = Math.max(25, minIndex);
            int endIdx = Math.min(30, maxIndex);

            // 从x轴开始
            float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(startIdx, 0).x;
            testFallingPath.moveTo(startX, xAxisY);

            // 创建模拟的下跌趋势线
            for (int i = startIdx; i <= endIdx; i++) {
                float x = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(i, 0).x;

                // 模拟逐渐下降的低点
                float progress = (float) (i - startIdx) / (endIdx - startIdx);
                float y = contentTop + (contentBottom - contentTop) * (0.5f + progress * 0.3f);

                testFallingPath.lineTo(x, y);
            }

            // 回到x轴闭合
            float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(endIdx, 0).x;
            testFallingPath.lineTo(endX, xAxisY);
            testFallingPath.close();

            // 设置渐变
            LinearGradient fallingGradient = new LinearGradient(
                    0, contentTop, 0, xAxisY,
                    Color.parseColor("#60F44336"),
                    Color.parseColor("#00F44336"),
                    Shader.TileMode.CLAMP
            );
            fallingRegionPaint.setShader(fallingGradient);

            canvas.drawPath(testFallingPath, fallingRegionPaint);
            fallingRegionPaint.setShader(null);

            Log.d(TAG, "Drew test falling trend shadow");
        }
    }

    /**
     * 绘制趋势区间背景（旧版本，保留作为备份）
     */
    private void drawTrendBackground(Canvas canvas, List<KLineEntry> regionEntries,
                                     Paint paint, float contentTop, float contentBottom) {
        if (regionEntries.size() < 2) return;

        KLineEntry firstEntry = regionEntries.get(0);
        KLineEntry lastEntry = regionEntries.get(regionEntries.size() - 1);

        // 获取区间的起始和结束X坐标
        float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(firstEntry.index - 0.5f, 0).x; // 稍微向左扩展
        float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(lastEntry.index + 0.5f, 0).x; // 稍微向右扩展

        // 获取区间内的最高点和最低点
        float maxHigh = Float.MIN_VALUE;
        float minLow = Float.MAX_VALUE;

        for (KLineEntry entry : regionEntries) {
            maxHigh = Math.max(maxHigh, entry.high);
            minLow = Math.min(minLow, entry.low);
        }

        // 转换为屏幕坐标
        float topY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(0, maxHigh).y;
        float bottomY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(0, minLow).y;

        // 确保在图表范围内
        topY = Math.max(topY, contentTop);
        bottomY = Math.min(bottomY, contentBottom);

        // 增加一些垂直边距，确保覆盖完整区间
        float verticalMargin = (bottomY - topY) * 0.2f; // 20%的垂直边距
        topY = Math.max(topY - verticalMargin, contentTop);
        bottomY = Math.min(bottomY + verticalMargin, contentBottom);

        Log.d(TAG, "Drawing background rect: startX=" + startX + ", endX=" + endX +
                ", topY=" + topY + ", bottomY=" + bottomY);

        // 简化绘制逻辑，先用纯色背景测试
        RectF backgroundRect = new RectF(startX, topY, endX, bottomY);
        canvas.drawRect(backgroundRect, paint);
    }
} 