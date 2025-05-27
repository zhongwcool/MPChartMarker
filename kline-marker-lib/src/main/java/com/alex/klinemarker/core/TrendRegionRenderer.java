package com.alex.klinemarker.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.Log;

import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.TrendRegion;
import com.github.mikephil.charting.charts.CombinedChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 趋势区间渲染器
 * 负责在K线图上绘制趋势区间背景
 */
public class TrendRegionRenderer<T> {

    private static final String TAG = "TrendRegionRenderer";

    private final Context context;
    private final CombinedChart chart;
    private final KLineDataAdapter<T> dataAdapter;
    private final TrendRegionConfig config;

    // 绘制相关的Paint对象
    private Paint trendRegionPaint;

    // 数据
    private List<T> klineData;
    private List<TrendRegion> trendRegions;

    // 屏幕密度
    private final float density;

    public TrendRegionRenderer(Context context, CombinedChart chart,
                               KLineDataAdapter<T> dataAdapter, TrendRegionConfig config) {
        this.context = context;
        this.chart = chart;
        this.dataAdapter = dataAdapter;
        this.config = config != null ? config : new TrendRegionConfig();
        this.density = context.getResources().getDisplayMetrics().density;
        this.trendRegions = new ArrayList<>();

        // 初始化Paint对象
        initPaints();
    }

    /**
     * 初始化绘制相关的Paint对象
     */
    private void initPaints() {
        trendRegionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trendRegionPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 设置K线数据
     */
    public void setKLineData(List<T> klineData) {
        this.klineData = klineData;
    }

    /**
     * 设置趋势区间数据
     */
    public void setTrendRegions(List<TrendRegion> trendRegions) {
        this.trendRegions = trendRegions != null ? trendRegions : new ArrayList<>();
        Log.d(TAG, "Set " + this.trendRegions.size() + " trend regions");
    }

    /**
     * 绘制趋势区间背景
     */
    public void drawTrendRegions(Canvas canvas) {
        if (klineData == null || trendRegions.isEmpty()) {
            return;
        }

        Log.d(TAG, "drawTrendRegions called with " + trendRegions.size() + " regions");

        // 获取图表可见区域的时间范围
        float minTime = chart.getLowestVisibleX();
        float maxTime = chart.getHighestVisibleX();

        // 获取图表边界
        float contentTop = chart.getViewPortHandler().contentTop();
        float contentBottom = chart.getViewPortHandler().contentBottom();

        // 为每个趋势区间绘制背景
        for (int regionIndex = 0; regionIndex < trendRegions.size(); regionIndex++) {
            TrendRegion region = trendRegions.get(regionIndex);
            Log.d(TAG, "Processing region: " + region.toString());

            // 查找该区间内的K线数据
            List<T> regionEntries = findEntriesInRegion(region, minTime, maxTime);

            if (regionEntries.isEmpty()) {
                Log.d(TAG, "No entries found in visible range for region: " + region.getStart() + " to " + region.getEnd());
                continue;
            }

            Log.d(TAG, "Found " + regionEntries.size() + " entries for region " + regionIndex);

            // 绘制区间背景
            drawRegionBackground(canvas, regionEntries, region, contentTop, contentBottom);
        }
    }

    /**
     * 查找指定趋势区间内的K线数据
     */
    private List<T> findEntriesInRegion(TrendRegion region, float minTime, float maxTime) {
        List<T> regionEntries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (T entry : klineData) {
            float xValue = dataAdapter.getXValue(entry);

            // 检查该K线条目是否在可见时间范围内
            if (xValue >= minTime && xValue <= maxTime) {
                // 检查该K线条目是否在趋势区间的时间范围内
                if (dataAdapter.getDate(entry) != null) {
                    String entryDateStr = dateFormat.format(dataAdapter.getDate(entry));
                    if (region.containsDate(entryDateStr)) {
                        regionEntries.add(entry);
                        Log.d(TAG, "Entry (date: " + entryDateStr + ") is in region " + region.getStart() + " to " + region.getEnd());
                    }
                }
            }
        }

        return regionEntries;
    }

    /**
     * 绘制区间背景
     */
    private void drawRegionBackground(Canvas canvas, List<T> regionEntries,
                                      TrendRegion region, float contentTop, float contentBottom) {
        if (regionEntries.isEmpty()) return;

        Log.d(TAG, "Drawing region background for " + regionEntries.size() + " entries");

        // 获取基础颜色
        int baseColor = getRegionColor(region);

        // 创建路径来绘制沿K线走势的背景
        Path backgroundPath = new Path();

        // 添加一些边距
        float dayMargin = 0.5f;

        // 获取区间的起始和结束X坐标
        T firstEntry = regionEntries.get(0);
        T lastEntry = regionEntries.get(regionEntries.size() - 1);

        float startX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(dataAdapter.getXValue(firstEntry) - dayMargin, 0).x;
        float endX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(dataAdapter.getXValue(lastEntry) + dayMargin, 0).x;

        // 从左下角开始
        backgroundPath.moveTo(startX, contentBottom);

        // 计算平滑的中点（如果启用平滑）
        List<Float> smoothedMidPoints = config.isEnableSmoothing() ?
                calculateSmoothedMidPoints(regionEntries) :
                calculateDirectMidPoints(regionEntries);

        // 沿着平滑的中点绘制上边沿
        for (int i = 0; i < regionEntries.size(); i++) {
            T entry = regionEntries.get(i);
            float smoothedMidPoint = smoothedMidPoints.get(i);

            // 使用平滑后的中点作为上边沿
            float x = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(dataAdapter.getXValue(entry), smoothedMidPoint).x;
            float y = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                    .getPixelForValues(dataAdapter.getXValue(entry), smoothedMidPoint).y;

            // 添加偏移
            float offsetPx = config.getOffsetDp() * density;
            y += offsetPx;

            if (i == 0) {
                // 第一个点，先连接到左上角，再到平滑中点
                backgroundPath.lineTo(startX, y);
                backgroundPath.lineTo(x, y);
            } else if (config.isEnableBezierCurve()) {
                // 使用贝塞尔曲线连接
                T prevEntry = regionEntries.get(i - 1);
                float prevSmoothedMidPoint = smoothedMidPoints.get(i - 1);
                float prevX = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(dataAdapter.getXValue(prevEntry), prevSmoothedMidPoint).x;
                float prevY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                        .getPixelForValues(dataAdapter.getXValue(prevEntry), prevSmoothedMidPoint).y;
                prevY += offsetPx;

                // 计算控制点
                float controlX = (prevX + x) / 2;
                float controlY = (prevY + y) / 2;

                // 使用二次贝塞尔曲线
                backgroundPath.quadTo(controlX, controlY, x, y);
            } else {
                // 直线连接
                backgroundPath.lineTo(x, y);
            }
        }

        // 闭合路径
        float lastSmoothedMidPoint = smoothedMidPoints.get(regionEntries.size() - 1);
        float lastY = (float) chart.getTransformer(chart.getAxisLeft().getAxisDependency())
                .getPixelForValues(dataAdapter.getXValue(lastEntry), lastSmoothedMidPoint).y;
        float offsetPx = config.getOffsetDp() * density;
        lastY += offsetPx;

        backgroundPath.lineTo(endX, lastY);
        backgroundPath.lineTo(endX, contentBottom);
        backgroundPath.close();

        // 设置画笔和渐变
        if (config.isEnableGradient()) {
            setupGradientPaint(baseColor, contentTop, contentBottom);
        } else {
            trendRegionPaint.setColor(Color.argb(
                    (int) (config.getTopAlpha() * 255),
                    Color.red(baseColor),
                    Color.green(baseColor),
                    Color.blue(baseColor)
            ));
            trendRegionPaint.setShader(null);
        }

        // 绘制路径
        canvas.drawPath(backgroundPath, trendRegionPaint);

        Log.d(TAG, "Drew trend region background path for " + regionEntries.size() + " entries");
    }

    /**
     * 获取区间颜色
     */
    private int getRegionColor(TrendRegion region) {
        switch (region.getType()) {
            case RISING:
                return config.getRisingColor();
            case FALLING:
                return config.getFallingColor();
            case NEUTRAL:
            default:
                return config.getNeutralColor();
        }
    }

    /**
     * 设置渐变画笔
     */
    private void setupGradientPaint(int baseColor, float topY, float bottomY) {
        float topAlpha = config.getTopAlpha();
        float bottomAlpha = config.getBottomAlpha();

        int topColor = Color.argb((int) (topAlpha * 255), Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
        int bottomColor = Color.argb((int) (bottomAlpha * 255), Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));

        LinearGradient gradient = new LinearGradient(
                0, topY,
                0, bottomY,
                topColor,
                bottomColor,
                Shader.TileMode.CLAMP
        );

        trendRegionPaint.setShader(gradient);
    }

    /**
     * 计算平滑的中点值
     */
    private List<Float> calculateSmoothedMidPoints(List<T> regionEntries) {
        List<Float> smoothedMidPoints = new ArrayList<>();
        int windowSize = Math.min(config.getSmoothWindowSize(), regionEntries.size());

        for (int i = 0; i < regionEntries.size(); i++) {
            float sum = 0;
            int count = 0;

            int start = Math.max(0, i - windowSize / 2);
            int end = Math.min(regionEntries.size() - 1, i + windowSize / 2);

            for (int j = start; j <= end; j++) {
                T entry = regionEntries.get(j);
                float midPoint = (dataAdapter.getOpen(entry) + dataAdapter.getClose(entry)) / 2;
                sum += midPoint;
                count++;
            }

            float smoothedMidPoint = sum / count;
            smoothedMidPoints.add(smoothedMidPoint);
        }

        return smoothedMidPoints;
    }

    /**
     * 计算直接的中点值（不平滑）
     */
    private List<Float> calculateDirectMidPoints(List<T> regionEntries) {
        List<Float> midPoints = new ArrayList<>();

        for (T entry : regionEntries) {
            float midPoint = (dataAdapter.getOpen(entry) + dataAdapter.getClose(entry)) / 2;
            midPoints.add(midPoint);
        }

        return midPoints;
    }
} 