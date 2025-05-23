package com.alex.mpchart.marker.ui.home;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alex.mpchart.marker.data.model.KLineEntry;
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
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private CombinedChart combinedChart;
    private BarChart volumeChart;
    private KLineMarker kLineMarker;
    private List<KLineEntry> kLineEntries;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 先获取图表引用
        combinedChart = binding.combinedChart;
        volumeChart = binding.barChart;

        // 设置图表联动
        setupChartLinkage();

        homeViewModel.getKLineData().observe(getViewLifecycleOwner(), this::showCharts);

        return root;
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
        showKLineChart(data);
        showVolumeChart(data);
    }

    private void showKLineChart(List<KLineEntry> data) {
        // CombinedChart chart = binding.combinedChart; // 使用全局变量

        // 计算最高和最低价格，用于后续设置Y轴范围
        float highestHigh = Float.MIN_VALUE;
        float lowestLow = Float.MAX_VALUE;

        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        for (KLineEntry entry : data) {
            candleEntries.add(new CandleEntry(entry.index, entry.high, entry.low, entry.open, entry.close));

            // 更新最高价和最低价
            highestHigh = Math.max(highestHigh, entry.high);
            lowestLow = Math.min(lowestLow, entry.low);
        }

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
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(8f);

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

        // 创建并设置标记
        kLineMarker = new KLineMarker(getContext(), combinedChart, data);
        combinedChart.setRenderer(new CustomCombinedChartRenderer(combinedChart,
                combinedChart.getAnimator(), combinedChart.getViewPortHandler(), kLineMarker));

        // 设置可视范围 - 在设置数据后设置
        combinedChart.setVisibleXRangeMaximum(50);

        // 不使用fitScreen，确保可视范围生效
        // combinedChart.fitScreen();

        // 移动到起始位置
        combinedChart.moveViewToX(0);
        combinedChart.zoom(1.0f, 1.0f, 0, 0);

        combinedChart.invalidate();
    }

    // 自定义渲染器，用于绘制标记
    private class CustomCombinedChartRenderer extends CombinedChartRenderer {
        private final KLineMarker marker;

        public CustomCombinedChartRenderer(CombinedChart chart,
                                           com.github.mikephil.charting.animation.ChartAnimator animator,
                                           ViewPortHandler viewPortHandler,
                                           KLineMarker marker) {
            super(chart, animator, viewPortHandler);
            this.marker = marker;
        }

        @Override
        public void drawData(Canvas c) {
            super.drawData(c);

            // 在绘制完数据后绘制标记
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
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(8f);

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
            barEntries.add(new BarEntry(entry.index, entry.volume));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setColor(Color.LTGRAY);
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);

        // 设置数据
        volumeChart.setData(barData);

        // 设置可视范围 - 在设置数据后设置
        volumeChart.setVisibleXRangeMaximum(50);

        // 不使用fitScreen，确保可视范围生效
        // volumeChart.fitScreen();

        // 移动到起始位置
        volumeChart.moveViewToX(0);
        volumeChart.zoom(1.0f, 1.0f, 0, 0);

        volumeChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}