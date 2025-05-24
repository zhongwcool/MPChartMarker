package com.alex.mpchart.marker.ui.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.alex.mpchart.marker.data.model.TrendRegion;
import com.alex.mpchart.marker.utils.TrendRegionParser;
import com.github.mikephil.charting.charts.CombinedChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 趋势区间绘制器
 * 根据指定的时间范围绘制趋势背景
 */
public class TrendRegionDrawer {
    private static final String TAG = "TrendRegionDrawer";
    private final Paint trendRegionPaint;
    private final CombinedChart chart;
    private final List<KLineEntry> entries;
    private List<TrendRegion> trendRegions;

    public TrendRegionDrawer(Context context, CombinedChart chart, List<KLineEntry> entries) {
        this.chart = chart;
        this.entries = entries;
        this.trendRegions = new ArrayList<>();

        // 初始化趋势区间画笔
        trendRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trendRegionPaint.setStyle(Paint.Style.FILL);

        Log.d(TAG, "TrendRegionDrawer initialized with " + entries.size() + " entries");
    }

    /**
     * 设置趋势区间数据
     *
     * @param trendRegions 趋势区间列表
     */
    public void setTrendRegions(List<TrendRegion> trendRegions) {
        this.trendRegions = trendRegions != null ? trendRegions : new ArrayList<>();
        Log.d(TAG, "Set " + this.trendRegions.size() + " trend regions");
    }

    /**
     * 绘制趋势区间背景
     */
    public void drawTrendRegions(Canvas canvas) {
        Log.d(TAG, "drawTrendRegions called with " + trendRegions.size() + " regions");

        if (trendRegions.isEmpty()) {
            Log.d(TAG, "No trend regions to draw");
            return;
        }

        // 获取图表可见区域的时间范围
        float minTime = chart.getLowestVisibleX();
        float maxTime = chart.getHighestVisibleX();

        Log.d(TAG, "Visible time range: " + minTime + " to " + maxTime);

        // 获取图表边界
        float contentTop = chart.getViewPortHandler().contentTop();
        float contentBottom = chart.getViewPortHandler().contentBottom();

        // 为每个趋势区间绘制背景
        for (int regionIndex = 0; regionIndex < trendRegions.size(); regionIndex++) {
            TrendRegion region = trendRegions.get(regionIndex);
            Log.d(TAG, "Processing region: " + region.toString());

            // 查找该区间内的K线数据
            List<KLineEntry> regionEntries = findEntriesInRegion(region, minTime, maxTime);

            if (regionEntries.isEmpty()) {
                Log.d(TAG, "No entries found in visible range for region: " + region.start + " to " + region.end);
                continue;
            }

            Log.d(TAG, "Found " + regionEntries.size() + " entries for region " + regionIndex);

            // 绘制区间背景
            drawRegionBackground(canvas, regionEntries, region, contentTop, contentBottom, regionIndex);
        }
    }

    /**
     * 查找指定趋势区间内的K线数据
     */
    private List<KLineEntry> findEntriesInRegion(TrendRegion region, float minTime, float maxTime) {
        List<KLineEntry> regionEntries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < entries.size(); i++) {
            KLineEntry entry = entries.get(i);

            // 检查该K线条目是否在可见时间范围内
            if (entry.getXValue() >= minTime && entry.getXValue() <= maxTime) {
                // 检查该K线条目是否在趋势区间的时间范围内
                if (entry.date != null) {
                    String entryDateStr = dateFormat.format(entry.date);
                    if (region.containsDate(entryDateStr)) {
                        regionEntries.add(entry);
                        Log.d(TAG, "Entry " + i + " (date: " + entryDateStr + ") is in region " + region.start + " to " + region.end);
                    }
                }
            }
        }

        return regionEntries;
    }

    /**
     * 绘制区间背景
     */
    private void drawRegionBackground(Canvas canvas, List<KLineEntry> regionEntries,
                                      TrendRegion region, float contentTop, float contentBottom, int regionIndex) {
        if (regionEntries.isEmpty()) return;

        Log.d(TAG, "Drawing region background for " + regionEntries.size() + " entries");

        // 使用渐变背景色方案
        int baseColor = Color.parseColor("#F44336"); // 红色

        // 创建路径来绘制沿K线走势的背景
        Path backgroundPath = new Path();

        // 添加一些边距
        float dayMargin = 0.5f;

        // 获取区间的起始和结束X坐标（确保左右边界平行）
        KLineEntry firstEntry = regionEntries.get(0);
        KLineEntry lastEntry = regionEntries.get(regionEntries.size() - 1);
        
        float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(firstEntry.getXValue() - dayMargin, 0).x;
        float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(lastEntry.getXValue() + dayMargin, 0).x;

        // 从左下角开始
        backgroundPath.moveTo(startX, contentBottom);

        // 计算平滑的上边沿点（使用开盘价和收盘价的中点，向下偏移3dp）
        List<Float> smoothedMidPoints = calculateSmoothedMidPoints(regionEntries);

        // 沿着平滑的中点绘制上边沿
        for (int i = 0; i < regionEntries.size(); i++) {
            KLineEntry entry = regionEntries.get(i);
            float smoothedMidPoint = smoothedMidPoints.get(i);

            // 使用平滑后的中点作为上边沿
            float x = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(entry.getXValue(), smoothedMidPoint).x;
            float y = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(entry.getXValue(), smoothedMidPoint).y;

            // 向下偏移3dp
            float density = chart.getContext().getResources().getDisplayMetrics().density;
            float offsetDp = 8 * density; // 8dp转换为像素
            y += offsetDp;

            if (i == 0) {
                // 第一个点，先连接到左上角，再到平滑中点
                backgroundPath.lineTo(startX, y);
                backgroundPath.lineTo(x, y);
            } else {
                // 使用二次贝塞尔曲线连接到当前点，创建更平滑的效果
                KLineEntry prevEntry = regionEntries.get(i - 1);
                float prevSmoothedMidPoint = smoothedMidPoints.get(i - 1);
                float prevX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(prevEntry.getXValue(), prevSmoothedMidPoint).x;
                float prevY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(prevEntry.getXValue(), prevSmoothedMidPoint).y;
                prevY += offsetDp; // 同样向下偏移3dp

                // 计算控制点，位于两点之间
                float controlX = (prevX + x) / 2;
                float controlY = (prevY + y) / 2;

                // 使用二次贝塞尔曲线
                backgroundPath.quadTo(controlX, controlY, x, y);
            }

            Log.d(TAG, "Point " + i + ": x=" + x + ", y=" + y + ", smoothedMidPoint=" + smoothedMidPoint + ", originalMidPoint=" + ((entry.open + entry.close) / 2));
        }

        // 从最后一个高点连接到右上角，再到右下角，然后回到起始点
        float lastSmoothedMidPoint = smoothedMidPoints.get(regionEntries.size() - 1);
        float lastY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(lastEntry.getXValue(), lastSmoothedMidPoint).y;
        float density = chart.getContext().getResources().getDisplayMetrics().density;
        float offsetDp = 8 * density;
        lastY += offsetDp;

        backgroundPath.lineTo(endX, lastY);
        backgroundPath.lineTo(endX, contentBottom);
        backgroundPath.close();

        // 计算渐变的顶部和底部Y坐标
        float topY = contentTop;
        float bottomY = contentBottom;

        // 创建渐变颜色
        float topAlpha = 0.30f;  // 上边沿透明度很低
        float bottomAlpha = 0.10f; // 下边沿透明度也较低，半透明效果

        int topColor = Color.argb((int) (topAlpha * 255), Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
        int bottomColor = Color.argb((int) (bottomAlpha * 255), Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));

        // 创建线性渐变着色器
        LinearGradient gradient = new LinearGradient(
                0, topY,           // 起始点 (x1, y1)
                0, bottomY,        // 结束点 (x2, y2)
                topColor,          // 起始颜色
                bottomColor,       // 结束颜色
                Shader.TileMode.CLAMP
        );

        // 设置画笔
        trendRegionPaint.setStyle(Paint.Style.FILL);
        trendRegionPaint.setShader(gradient);

        // 绘制路径
        canvas.drawPath(backgroundPath, trendRegionPaint);

        Log.d(TAG, "Drew trend region background path with gradient for " + regionEntries.size() + " entries");
    }

    /**
     * 计算平滑的中点值
     * 使用简单移动平均来平滑K线开盘价和收盘价的中点
     */
    private List<Float> calculateSmoothedMidPoints(List<KLineEntry> regionEntries) {
        List<Float> smoothedMidPoints = new ArrayList<>();
        int windowSize = Math.min(3, regionEntries.size()); // 使用3点移动平均，或更小的窗口

        for (int i = 0; i < regionEntries.size(); i++) {
            float sum = 0;
            int count = 0;

            // 计算当前点周围的移动平均
            int start = Math.max(0, i - windowSize / 2);
            int end = Math.min(regionEntries.size() - 1, i + windowSize / 2);

            for (int j = start; j <= end; j++) {
                // 计算开盘价和收盘价的中点
                float midPoint = (regionEntries.get(j).open + regionEntries.get(j).close) / 2;
                sum += midPoint;
                count++;
            }

            float smoothedMidPoint = sum / count;
            smoothedMidPoints.add(smoothedMidPoint);

            float originalMidPoint = (regionEntries.get(i).open + regionEntries.get(i).close) / 2;
            Log.d(TAG, "Smoothed midpoint " + i + ": original=" + originalMidPoint + ", smoothed=" + smoothedMidPoint);
        }

        return smoothedMidPoints;
    }

    /**
     * 根据JSON数据设置趋势区间
     * 兼容用户提供的数据格式
     */
    public void setTrendRegionsFromJson(String jsonData) {
        Log.d(TAG, "Setting trend regions from JSON data");
        List<TrendRegion> regions = TrendRegionParser.parseFromJson(jsonData);
        setTrendRegions(regions);
    }
} 