package com.alex.mpchart.marker.ui.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.alex.mpchart.marker.data.model.TrendRegion;
import com.alex.mpchart.marker.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private CombinedChart combinedChart;
    private BarChart volumeChart;
    private KLineMarker kLineMarker;
    private TrendRegionDrawer trendRegionDrawer;
    private List<KLineEntry> kLineEntries;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 初始化SwipeRefreshLayout
        swipeRefreshLayout = binding.swipeRefreshLayout;
        setupSwipeRefresh();

        // 先获取图表引用
        combinedChart = binding.combinedChart;
        volumeChart = binding.barChart;

        // 设置图表联动
        setupChartLinkage();

        // 观察数据变化
        homeViewModel.getKLineData().observe(getViewLifecycleOwner(), this::showCharts);

        // 观察刷新状态
        homeViewModel.getIsRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        });

        return root;
    }

    private void setupSwipeRefresh() {
        // 设置下拉刷新的颜色
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        // 设置下拉刷新的背景色
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        // 设置下拉距离
        swipeRefreshLayout.setDistanceToTriggerSync(200);

        // 设置刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> {
            homeViewModel.refreshData();
        });
    }

    private void setupChartLinkage() {
        // 为K线图添加手势监听
        combinedChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 当K线图滑动时，同步滑动成交量图
                if (volumeChart != null) {
                    // 获取K线图的可视范围
                    float lowestX = combinedChart.getLowestVisibleX();
                    volumeChart.moveViewToX(lowestX);
                    volumeChart.invalidate();
                }

                // 滑动时重绘标记
                if (combinedChart != null && kLineMarker != null) {
                    combinedChart.invalidate();
                }
            }
        });

        // 为成交量图添加手势监听
        volumeChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 当成交量图滑动时，同步滑动K线图
                if (combinedChart != null) {
                    // 获取成交量图的可视范围
                    float lowestX = volumeChart.getLowestVisibleX();
                    combinedChart.moveViewToX(lowestX);
                    combinedChart.invalidate();
                }
            }
        });
    }

    private void showCharts(List<KLineEntry> data) {
        this.kLineEntries = data;

        // 更新时间显示
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
        String updateTime = "最后更新: " + sdf.format(new Date());
        binding.textHome.setText(updateTime);
        binding.textHome.setVisibility(View.VISIBLE);
        
        showKLineChart(data);
        showVolumeChart(data);
    }

    private void showKLineChart(List<KLineEntry> data) {
        // CombinedChart chart = binding.combinedChart; // 使用全局变量

        // 计算最高和最低价格，用于后续设置Y轴范围
        float highestHigh = Float.MIN_VALUE;
        float lowestLow = Float.MAX_VALUE;

        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            KLineEntry entry = data.get(i);
            // 使用相对天数作为x轴坐标
            float xValue = entry.getXValue();
            candleEntries.add(new CandleEntry(xValue, entry.high, entry.low, entry.open, entry.close));

            // 更新最高价和最低价
            highestHigh = Math.max(highestHigh, entry.high);
            lowestLow = Math.min(lowestLow, entry.low);

            // 添加调试日志
            if (i < 5 || i >= data.size() - 5) {
                SimpleDateFormat debugFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Log.d("HomeFragment", "Entry " + i + ": date=" + debugFormat.format(entry.date) +
                        ", xValue=" + xValue + ", open=" + entry.open + ", close=" + entry.close);
            }
        }

        Log.d("HomeFragment", "Created " + candleEntries.size() + " candle entries, price range: " +
                lowestLow + " to " + highestHigh);

        // 先设置视图属性，然后再设置数据
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE});

        // 恢复设置视图边距，确保有足够的显示空间
        float density = getResources().getDisplayMetrics().density;
        int leftRight = (int) (10 * density); // 10dp左右边距
        int topBottom = (int) (20 * density); // 20dp上下边距
        combinedChart.setViewPortOffsets(leftRight, topBottom, leftRight, topBottom);

        combinedChart.setExtraTopOffset(10f);
        combinedChart.setExtraBottomOffset(10f);
        combinedChart.setExtraLeftOffset(5f);
        combinedChart.setExtraRightOffset(5f);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(8f);

        // 设置日期格式化器
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                // 将相对天数转换回日期
                long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
                long timestamp = baseTime + (long) (value * 24 * 60 * 60 * 1000);
                return dateFormat.format(new Date(timestamp));
            }
        });

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextSize(8f);

        // 增加Y轴边距到20%
        float priceRange = highestHigh - lowestLow;
        leftAxis.setAxisMinimum(lowestLow - priceRange * 0.2f);
        leftAxis.setAxisMaximum(highestHigh + priceRange * 0.2f);

        // 设置Y轴分段数
        leftAxis.setLabelCount(6, true);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

        combinedChart.getLegend().setEnabled(false);
        combinedChart.setPadding(0, 0, 0, 0);

        // 缩放和可视范围设置
        combinedChart.setAutoScaleMinMaxEnabled(false);
        combinedChart.setScaleEnabled(false);
        combinedChart.setDoubleTapToZoomEnabled(false);
        combinedChart.setScaleXEnabled(false);
        combinedChart.setScaleYEnabled(false);
        combinedChart.setPinchZoom(false);

        // 准备数据
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "K线");
        candleDataSet.setColor(Color.rgb(80, 80, 80));
        candleDataSet.setShadowColor(Color.DKGRAY);
        candleDataSet.setShadowWidth(0.7f);
        candleDataSet.setDecreasingColor(Color.RED);
        candleDataSet.setDecreasingPaintStyle(android.graphics.Paint.Style.FILL);
        candleDataSet.setIncreasingColor(Color.GREEN);
        candleDataSet.setIncreasingPaintStyle(android.graphics.Paint.Style.FILL);
        candleDataSet.setNeutralColor(Color.BLUE);
        candleDataSet.setDrawValues(false);

        CandleData candleData = new CandleData(candleDataSet);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(candleData);

        // 设置数据
        combinedChart.setData(combinedData);

        // 创建并设置标记和背景绘制器
        kLineMarker = new KLineMarker(getContext(), combinedChart, data);
        trendRegionDrawer = new TrendRegionDrawer(getContext(), combinedChart, data);

        // 设置趋势区间数据（使用示例数据）
        setTrendRegionsExample();
        
        combinedChart.setRenderer(new CustomCombinedChartRenderer(combinedChart,
                combinedChart.getAnimator(), combinedChart.getViewPortHandler(),
                kLineMarker, trendRegionDrawer));

        // 设置可视范围 - 在设置数据后设置
        combinedChart.setVisibleXRangeMaximum(50);

        // 计算数据的实际范围并移动到最新数据位置
        if (!data.isEmpty()) {
            float minX = data.get(0).getXValue();
            float maxX = data.get(data.size() - 1).getXValue();
            Log.d("HomeFragment", "Data X range: " + minX + " to " + maxX);

            // 移动到最新数据位置（最右侧）
            combinedChart.moveViewToX(maxX - 25); // 显示最后25天的数据
        } else {
            combinedChart.moveViewToX(0);
        }
        
        combinedChart.zoom(1.0f, 1.0f, 0, 0);

        combinedChart.invalidate();
    }

    // 自定义渲染器，用于绘制标记
    private class CustomCombinedChartRenderer extends CombinedChartRenderer {
        private final KLineMarker marker;
        private final TrendRegionDrawer trendRegionDrawer;

        public CustomCombinedChartRenderer(CombinedChart chart,
                                           com.github.mikephil.charting.animation.ChartAnimator animator,
                                           ViewPortHandler viewPortHandler,
                                           KLineMarker marker,
                                           TrendRegionDrawer trendRegionDrawer) {
            super(chart, animator, viewPortHandler);
            this.marker = marker;
            this.trendRegionDrawer = trendRegionDrawer;
        }

        @Override
        public void drawData(Canvas c) {
            // 先绘制趋势区间背景
            if (trendRegionDrawer != null) {
                trendRegionDrawer.drawTrendRegions(c);
            }

            // 再绘制K线数据
            super.drawData(c);

            // 最后绘制标记（在K线之上）
            if (marker != null) {
                marker.drawMarkers(c);
            }
        }
    }

    private void showVolumeChart(List<KLineEntry> data) {
        // BarChart barChart = binding.barChart; // 使用全局变量

        // 找出最大成交量用于计算Y轴范围
        float maxVolume = Float.MIN_VALUE;
        for (KLineEntry entry : data) {
            maxVolume = Math.max(maxVolume, entry.volume);
        }

        // 先设置视图属性
        volumeChart.getDescription().setEnabled(false);
        volumeChart.setDrawGridBackground(false);
        volumeChart.setDrawBarShadow(false);

        // 恢复设置视图边距，确保有足够的显示空间
        float density = getResources().getDisplayMetrics().density;
        int leftRight = (int) (10 * density); // 10dp左右边距
        int topBottom = (int) (20 * density); // 20dp上下边距
        volumeChart.setViewPortOffsets(leftRight, topBottom, leftRight, topBottom);

        volumeChart.setExtraTopOffset(10f);
        volumeChart.setExtraBottomOffset(10f);
        volumeChart.setExtraLeftOffset(5f);
        volumeChart.setExtraRightOffset(5f);

        XAxis xAxis = volumeChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(8f);

        // 设置日期格式化器
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                // 将相对天数转换回日期
                long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
                long timestamp = baseTime + (long) (value * 24 * 60 * 60 * 1000);
                return dateFormat.format(new Date(timestamp));
            }
        });

        YAxis leftAxis = volumeChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        // 设置Y轴范围，顶部留20%的空间，底部从0开始
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(maxVolume * 1.2f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextSize(8f);
        // 设置Y轴分段数
        leftAxis.setLabelCount(4, true);

        YAxis rightAxis = volumeChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

        volumeChart.getLegend().setEnabled(false);
        volumeChart.setPadding(0, 0, 0, 0);

        // 缩放设置
        volumeChart.setAutoScaleMinMaxEnabled(false);
        volumeChart.setScaleEnabled(false);
        volumeChart.setDoubleTapToZoomEnabled(false);
        volumeChart.setScaleXEnabled(false);
        volumeChart.setScaleYEnabled(false);
        volumeChart.setPinchZoom(false);

        // 准备数据
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (KLineEntry entry : data) {
            // 使用相对天数作为x轴坐标
            float xValue = entry.getXValue();
            barEntries.add(new BarEntry(xValue, entry.volume));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setColor(Color.LTGRAY);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);

        // 设置数据
        volumeChart.setData(barData);

        // 设置可视范围 - 在设置数据后设置
        volumeChart.setVisibleXRangeMaximum(50);

        // 移动到与K线图相同的位置
        if (!data.isEmpty()) {
            float maxX = data.get(data.size() - 1).getXValue();
            volumeChart.moveViewToX(maxX - 25); // 显示最后25天的数据
        } else {
            volumeChart.moveViewToX(0);
        }
        
        volumeChart.zoom(1.0f, 1.0f, 0, 0);

        volumeChart.invalidate();
    }

    /**
     * 设置趋势区间示例数据
     * 根据用户提供的JSON格式创建趋势区间
     */
    private void setTrendRegionsExample() {
        if (trendRegionDrawer == null) return;

        // 方法1：直接创建趋势区间列表
        List<TrendRegion> regions = new ArrayList<>();

        // 获取当前日期并计算相对日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // 第一个区间：从15天前开始到最后（size=2）
        calendar.add(Calendar.DAY_OF_YEAR, -15);
        String start1 = dateFormat.format(calendar.getTime());
        regions.add(new TrendRegion(start1, null, 2, "2025-05-22T07:09:33.289230Z"));

        // 第二个区间：从80天前到75天前（size=3）
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -80);
        String start2 = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 5); // 5天的区间
        String end2 = dateFormat.format(calendar.getTime());
        regions.add(new TrendRegion(start2, end2, 3, "2025-05-22T06:46:10.313136Z"));

        // 第三个区间：从50天前到45天前（size=1）- 额外测试区间
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -50);
        String start3 = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 5);
        String end3 = dateFormat.format(calendar.getTime());
        regions.add(new TrendRegion(start3, end3, 1, "2025-05-22T05:30:00.000000Z"));

        // 设置趋势区间
        trendRegionDrawer.setTrendRegions(regions);

        Log.d("HomeFragment", "Set " + regions.size() + " trend regions:");
        for (TrendRegion region : regions) {
            Log.d("HomeFragment", "  Region: " + region.toString());
        }
    }

    /**
     * 演示如何使用JSON数据设置趋势区间
     * 这个方法展示了如何使用用户提供的JSON格式数据
     */
    private void setTrendRegionsFromJsonExample() {
        if (trendRegionDrawer == null) return;

        // 创建示例JSON数据（用户提供的格式）
        String jsonData = "{\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T07:09:33.289230Z\",\n" +
                "      \"start\": \"2025-05-15\",\n" +
                "      \"end\": null,\n" +
                "      \"size\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T06:46:10.313136Z\",\n" +
                "      \"start\": \"2025-03-19\",\n" +
                "      \"end\": \"2025-03-24\",\n" +
                "      \"size\": 3\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 使用JSON数据设置趋势区间
        trendRegionDrawer.setTrendRegionsFromJson(jsonData);

        Log.d("HomeFragment", "Set trend regions from JSON data");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}