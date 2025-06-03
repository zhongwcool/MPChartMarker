package com.alex.klinemarker;

import android.content.Context;
import android.graphics.Canvas;

import com.alex.klinemarker.core.KLineMarkerRenderer;
import com.alex.klinemarker.core.TrendRegionConfig;
import com.alex.klinemarker.core.TrendRegionRenderer;
import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.TrendRegion;
import com.alex.klinemarker.utils.TrendRegionParser;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * K线标记管理器
 * 这是库的主要入口类，提供简单易用的API来为K线图添加标记功能和趋势区间功能
 */
public class KLineMarkerManager<T> {

    private final Context context;
    private final CombinedChart chart;
    private final KLineDataAdapter<T> dataAdapter;
    private final KLineMarkerRenderer<T> markerRenderer;
    private final TrendRegionRenderer<T> trendRegionRenderer;
    private CustomCombinedChartRenderer customRenderer;

    /**
     * 构造函数
     *
     * @param context     Android上下文
     * @param chart       MPAndroidChart的CombinedChart实例
     * @param dataAdapter K线数据适配器，用于适配不同的K线数据格式
     */
    public KLineMarkerManager(Context context, CombinedChart chart, KLineDataAdapter<T> dataAdapter) {
        this(context, chart, dataAdapter, null, null);
    }

    /**
     * 构造函数（带自定义配置）
     *
     * @param context      Android上下文
     * @param chart        MPAndroidChart的CombinedChart实例
     * @param dataAdapter  K线数据适配器
     * @param markerConfig 标记配置，为null时使用默认配置
     */
    public KLineMarkerManager(Context context, CombinedChart chart,
                              KLineDataAdapter<T> dataAdapter, MarkerConfig markerConfig) {
        this(context, chart, dataAdapter, markerConfig, null);
    }

    /**
     * 构造函数（带完整自定义配置）
     *
     * @param context           Android上下文
     * @param chart             MPAndroidChart的CombinedChart实例
     * @param dataAdapter       K线数据适配器
     * @param markerConfig      标记配置，为null时使用默认配置
     * @param trendRegionConfig 趋势区间配置，为null时使用默认配置
     */
    public KLineMarkerManager(Context context, CombinedChart chart,
                              KLineDataAdapter<T> dataAdapter, MarkerConfig markerConfig,
                              TrendRegionConfig trendRegionConfig) {
        this.context = context;
        this.chart = chart;
        this.dataAdapter = dataAdapter;
        this.markerRenderer = new KLineMarkerRenderer<>(context, chart, dataAdapter);
        this.trendRegionRenderer = new TrendRegionRenderer<>(context, chart, dataAdapter, trendRegionConfig);

        // 设置自定义渲染器
        setupCustomRenderer();
    }

    /**
     * 设置自定义渲染器
     */
    private void setupCustomRenderer() {
        customRenderer = new CustomCombinedChartRenderer(
                chart,
                chart.getAnimator(),
                chart.getViewPortHandler(),
                markerRenderer,
                trendRegionRenderer
        );
        chart.setRenderer(customRenderer);
    }

    /**
     * 设置K线数据
     *
     * @param klineData K线数据列表
     */
    public void setKLineData(List<T> klineData) {
        markerRenderer.setKLineData(klineData);
        trendRegionRenderer.setKLineData(klineData);
    }

    /**
     * 设置标记数据
     *
     * @param markers 标记数据列表
     */
    public void setMarkers(List<MarkerData> markers) {
        markerRenderer.setMarkers(markers);
    }

    /**
     * 设置趋势区间数据
     *
     * @param trendRegions 趋势区间数据列表
     */
    public void setTrendRegions(List<TrendRegion> trendRegions) {
        trendRegionRenderer.setTrendRegions(trendRegions);
    }

    /**
     * 从JSON数据设置趋势区间
     *
     * @param jsonData JSON格式的趋势区间数据
     */
    public void setTrendRegionsFromJson(String jsonData) {
        List<TrendRegion> regions = TrendRegionParser.parseFromJson(jsonData);
        setTrendRegions(regions);
    }

    /**
     * 添加单个标记
     *
     * @param marker 标记数据
     */
    public void addMarker(MarkerData marker) {
        // 这里可以实现添加单个标记的逻辑
        // 为了简化，建议使用setMarkers方法
    }

    /**
     * 清除所有标记
     */
    public void clearMarkers() {
        markerRenderer.setMarkers(null);
        chart.invalidate();
    }

    /**
     * 清除所有趋势区间
     */
    public void clearTrendRegions() {
        trendRegionRenderer.setTrendRegions(null);
        chart.invalidate();
    }

    /**
     * 清除所有标记和趋势区间
     */
    public void clearAll() {
        clearMarkers();
        clearTrendRegions();
    }

    /**
     * 刷新图表显示
     */
    public void refresh() {
        chart.invalidate();
    }

    /**
     * 获取标记渲染器（高级用法）
     *
     * @return 标记渲染器实例
     */
    public KLineMarkerRenderer<T> getMarkerRenderer() {
        return markerRenderer;
    }

    /**
     * 获取趋势区间渲染器（高级用法）
     *
     * @return 趋势区间渲染器实例
     */
    public TrendRegionRenderer<T> getTrendRegionRenderer() {
        return trendRegionRenderer;
    }

    /**
     * 自定义图表渲染器
     * 在原有渲染基础上添加趋势区间背景和标记绘制功能
     */
    private static class CustomCombinedChartRenderer extends CombinedChartRenderer {
        private final KLineMarkerRenderer<?> markerRenderer;
        private final TrendRegionRenderer<?> trendRegionRenderer;

        public CustomCombinedChartRenderer(CombinedChart chart,
                                           com.github.mikephil.charting.animation.ChartAnimator animator,
                                           ViewPortHandler viewPortHandler,
                                           KLineMarkerRenderer<?> markerRenderer,
                                           TrendRegionRenderer<?> trendRegionRenderer) {
            super(chart, animator, viewPortHandler);
            this.markerRenderer = markerRenderer;
            this.trendRegionRenderer = trendRegionRenderer;
        }

        @Override
        public void drawData(Canvas c) {
            // 先绘制趋势区间背景（最底层）
            if (trendRegionRenderer != null) {
                try {
                    trendRegionRenderer.drawTrendRegions(c);
                } catch (Exception e) {
                    // 防止趋势区间绘制错误影响主图表
                    android.util.Log.w("ChartRenderer", "Error drawing trend regions", e);
                }
            }

            // 再绘制原有的图表数据（K线等）
            super.drawData(c);

            // 最后绘制标记（在图表数据之上）
            if (markerRenderer != null) {
                try {
                    markerRenderer.drawMarkers(c);
                } catch (Exception e) {
                    // 防止标记绘制错误影响主图表
                    android.util.Log.w("ChartRenderer", "Error drawing markers", e);
                }
            }
        }
    }

    /**
     * 创建Builder用于链式调用
     */
    public static class Builder<T> {
        private Context context;
        private CombinedChart chart;
        private KLineDataAdapter<T> dataAdapter;
        private MarkerConfig markerConfig;
        private TrendRegionConfig trendRegionConfig;

        public Builder<T> context(Context context) {
            this.context = context;
            return this;
        }

        public Builder<T> chart(CombinedChart chart) {
            this.chart = chart;
            return this;
        }

        public Builder<T> dataAdapter(KLineDataAdapter<T> dataAdapter) {
            this.dataAdapter = dataAdapter;
            return this;
        }

        public Builder<T> markerConfig(MarkerConfig config) {
            this.markerConfig = config;
            return this;
        }

        public Builder<T> trendRegionConfig(TrendRegionConfig config) {
            this.trendRegionConfig = config;
            return this;
        }

        // 保持向后兼容性
        public Builder<T> config(MarkerConfig config) {
            this.markerConfig = config;
            return this;
        }

        public KLineMarkerManager<T> build() {
            if (context == null || chart == null || dataAdapter == null) {
                throw new IllegalArgumentException("Context, Chart and DataAdapter are required");
            }
            return new KLineMarkerManager<>(context, chart, dataAdapter, markerConfig, trendRegionConfig);
        }
    }
} 