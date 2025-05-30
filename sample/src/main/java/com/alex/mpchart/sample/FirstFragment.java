package com.alex.mpchart.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.alex.klinemarker.KLineMarkerManager;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.core.TrendRegionConfig;
import com.alex.klinemarker.data.LineLength;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerPosition;
import com.alex.klinemarker.data.MarkerPresets;
import com.alex.klinemarker.data.MarkerShape;
import com.alex.klinemarker.data.TrendRegion;
import com.alex.mpchart.sample.databinding.FragmentFirstBinding;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private CombinedChart chart;
    private KLineMarkerManager<KLineData> markerManager;
    private List<KLineData> klineDataList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initChart();
        initMarkerManager();
        setupEventListeners();
        loadSampleData();
    }

    private void initChart() {
        chart = binding.chart;

        // 配置图表
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(true);
        chart.setGridBackgroundColor(Color.parseColor("#FAFAFA"));
        chart.setDrawBorders(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // 设置图表边距为0，不要有空隙
        chart.setExtraOffsets(2f, 20f, 2f, 50f);

        // 设置视口边距：左、顶、右为0，保留底部空间给X轴标签
        // 参数顺序：left, top, right, bottom
        chart.setViewPortOffsets(2f, 5f, 2f, 50f);

        // 配置X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#E8E8E8"));
        xAxis.setGridLineWidth(0.5f);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setTextSize(10f);
        xAxis.setAxisLineColor(Color.parseColor("#CCCCCC"));
        xAxis.setAxisLineWidth(1f);
        xAxis.setLabelCount(6, false);
        // 保留X轴标签显示
        xAxis.setDrawLabels(true);
        // 设置X轴的额外空间
        xAxis.setSpaceMin(0.5f); // 在最小值前添加空间
        xAxis.setSpaceMax(0.5f); // 在最大值后添加空间
        // 设置X轴标签格式为MM-dd
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                // 先检查klineDataList是否已初始化
                if (klineDataList == null || klineDataList.isEmpty()) {
                    return "";
                }

                int index = (int) value;
                if (index >= 0 && index < klineDataList.size()) {
                    return dateFormat.format(klineDataList.get(index).getDate());
                }
                return "";
            }
        });

        // 配置左Y轴
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E8E8E8"));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setTextSize(10f);
        leftAxis.setDrawAxisLine(false); // 不显示Y轴线
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setLabelCount(8, false);
        leftAxis.setXOffset(5f); // 设置标签距离图表边缘的偏移量
        leftAxis.setYOffset(-5f); // 垂直偏移，避免与网格线重叠
        // 设置Y轴的顶部空间边距为0，保留底部空间
        leftAxis.setSpaceTop(0f);
        leftAxis.setSpaceBottom(0f);

        // 配置右Y轴
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // 设置图例
        chart.getLegend().setEnabled(false);

        // 设置视口
        chart.setVisibleXRangeMaximum(50f); // 最大显示50个K线
        chart.setVisibleXRangeMinimum(5f);

        // 禁用自动缩放，避免产生额外边距
        chart.setAutoScaleMinMaxEnabled(false);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(true);
    }

    private void initMarkerManager() {
        // 创建数据适配器
        KLineDataAdapterImpl adapter = new KLineDataAdapterImpl();

        // 创建标记配置 - 调整尺寸
        MarkerConfig markerConfig = new MarkerConfig.Builder()
                .markerSize(12f) // 调整标记大小 (从8f改为12f)
                .textSize(8f) // 相应调整文字大小 (从7f改为8f)
                .padding(2f) // 保持内边距
                .buyColor(Color.parseColor("#00AA00"))
                .sellColor(Color.parseColor("#FF4444"))
                .numberColor(Color.parseColor("#2196F3")) 
                .upTriangleColor(Color.parseColor("#FF6600"))
                .downTriangleColor(Color.parseColor("#9C27B0"))
                .textColor(Color.WHITE)
                .numberTextColor(Color.BLACK) 
                .build();

        // 创建趋势区间配置
        TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
                .risingColor(Color.parseColor("#00AA00"))
                .fallingColor(Color.parseColor("#FF4444"))
                .neutralColor(Color.parseColor("#4285F4"))
                .topAlpha(0.15f) // 降低透明度以减少渲染负担
                .bottomAlpha(0.03f) // 降低透明度
                .offsetDp(4f)
                .enablePerformanceMode(true) // 启用性能模式
                .maxVisibleRegions(5) // 限制最大可见区间数量
                .build();

        // 初始化标记管理器
        markerManager = new KLineMarkerManager.Builder<KLineData>()
                .context(requireContext())
                .chart(chart)
                .dataAdapter(adapter)
                .markerConfig(markerConfig)
                .trendRegionConfig(trendConfig)
                .build();
    }

    private void setupEventListeners() {
        // 导航按钮
        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        // 添加标记按钮
        binding.btnAddMarker.setOnClickListener(v -> addSampleMarkers());

        // 添加趋势区间按钮
        binding.btnAddTrend.setOnClickListener(v -> addSampleTrendRegions());

        // 清除标记按钮
        binding.btnClearMarkers.setOnClickListener(v -> {
            markerManager.clearMarkers();
            Toast.makeText(getContext(), "已清除所有标记", Toast.LENGTH_SHORT).show();
        });

        // 清除全部按钮
        binding.btnClearAll.setOnClickListener(v -> {
            markerManager.clearAll();
            Toast.makeText(getContext(), "已清除所有标记和趋势区间", Toast.LENGTH_SHORT).show();
        });

        // 添加图表触摸监听器以监控性能
        setupChartTouchListener();
    }

    /**
     * 设置图表触摸监听器，用于监控滑动性能
     */
    private void setupChartTouchListener() {
        chart.setOnTouchListener((v, event) -> {
            // 记录触摸开始时间用于性能监控
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });
    }

    private void loadSampleData() {
        klineDataList = generateSampleKLineData();

        // 添加调试信息
        if (klineDataList.isEmpty()) {
            Toast.makeText(getContext(), "数据生成失败", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("loadSampleData", "生成了 " + klineDataList.size() + " 条K线数据");

        // 设置K线数据到标记管理器
        markerManager.setKLineData(klineDataList);

        // 创建K线图数据
        List<CandleEntry> candleEntries = new ArrayList<>();

        for (KLineData data : klineDataList) {
            CandleEntry entry = new CandleEntry(
                    data.getXValue(),
                    data.getHigh(),
                    data.getLow(),
                    data.getOpen(),
                    data.getClose()
            );

            candleEntries.add(entry);
        }

        // 添加调试信息
        Log.d("loadSampleData", "创建了 " + candleEntries.size() + " 个CandleEntry");

        // 创建K线数据集（全部实心）
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "K线");
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 影线配置
        candleDataSet.setShadowColor(Color.parseColor("#666666"));
        candleDataSet.setShadowWidth(1.0f);

        // 下跌K线配置（红色实心）
        candleDataSet.setDecreasingColor(Color.parseColor("#FF4444"));
        candleDataSet.setDecreasingPaintStyle(android.graphics.Paint.Style.FILL);

        // 上涨K线配置（绿色实心）
        candleDataSet.setIncreasingColor(Color.parseColor("#00AA00"));
        candleDataSet.setIncreasingPaintStyle(android.graphics.Paint.Style.FILL);

        // 平盘K线配置
        candleDataSet.setNeutralColor(Color.parseColor("#888888"));

        // 其他配置
        candleDataSet.setDrawValues(false);
        candleDataSet.setBarSpace(0.1f); // 减小K线间距从0.2f到0.1f
        candleDataSet.setShowCandleBar(true);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(Color.parseColor("#FFBB33"));

        CandleData candleData = new CandleData(candleDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(candleData);

        chart.setData(combinedData);

        // 暂时使用默认的自动范围，不设置精确范围
        // 让图表自动计算合适的显示范围

        // 先让图表完成初始化
        chart.invalidate();

        // 使用post确保在图表渲染完成后设置显示范围
        chart.post(() -> {
            // 计算缩放比例，使得显示50个K线
            float totalData = klineDataList.size();
            float targetVisible = 50f;
            float scaleX = totalData / targetVisible;

            // 设置缩放和位置
            chart.zoom(scaleX, 1f, 0, 0);
            // 移动视图，确保最右边有足够空间
            // 使用总数据量减去可见数量，再加上一些偏移
            float xPosition = Math.max(0, totalData - targetVisible + 1);
            chart.moveViewToX(xPosition);

            Log.d("loadSampleData", "显示范围已设置为50个K线");
        });

        Toast.makeText(getContext(), "K线数据加载完成", Toast.LENGTH_SHORT).show();
    }

    private List<KLineData> generateSampleKLineData() {
        List<KLineData> dataList = new ArrayList<>();
        float basePrice = 100f;

        // 获取当前日期，然后往前推100天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -99); // 往前推99天，加上今天共100天

        // 生成过去100天的K线数据
        for (int i = 0; i < 100; i++) {
            Date date = calendar.getTime();

            // 模拟更真实的价格波动
            float volatility = 0.03f; // 3%的波动率，让K线体更明显
            float trend = (float) Math.sin(i * 0.2) * 0.8f; // 增强趋势性

            float open = basePrice;
            float priceChange = (float) (Math.random() - 0.5) * basePrice * volatility + trend;
            float close = open + priceChange;

            // 确保有足够的K线体大小
            float minBodySize = basePrice * 0.008f; // 最小K线体为价格的0.8%
            if (Math.abs(close - open) < minBodySize) {
                if (close > open) {
                    close = open + minBodySize;
                } else {
                    close = open - minBodySize;
                }
            }

            // 确保高低价合理
            float bodySize = Math.abs(close - open);
            float wickSize = bodySize * (0.8f + (float) Math.random() * 2.0f);

            float high = Math.max(open, close) + wickSize * (float) Math.random();
            float low = Math.min(open, close) - wickSize * (float) Math.random();

            // 确保价格不为负
            if (low < 1) low = 1;

            float volume = 1000 + (float) Math.random() * 5000;

            KLineData klineData = new KLineData(date, open, high, low, close, volume);
            klineData.setIndex(i); // 设置索引
            dataList.add(klineData);
            basePrice = close * 0.8f + basePrice * 0.2f; // 调整平滑系数

            // 移动到下一天
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dataList;
    }

    private void addSampleMarkers() {
        List<MarkerData> markers = new ArrayList<>();

        if (klineDataList != null && !klineDataList.isEmpty()) {
            int dataSize = klineDataList.size();

            // 确保有足够的数据点来展示所有标记类型
            if (dataSize < 50) {
                Toast.makeText(getContext(), "数据点不足，需要至少50个数据点来展示所有标记类型", Toast.LENGTH_LONG).show();
                return;
            }

            // ========== 预定义标记样式 ==========

            // 1. 买入标记
            markers.add(MarkerData.createBuyMarker(
                    klineDataList.get(5).getDate(),
                    "B"  // 单个字母
            ));

            // 2. 卖出标记
            markers.add(MarkerData.createSellMarker(
                    klineDataList.get(15).getDate(),
                    "S"  // 单个字母
            ));

            // 3. 事件标记
            markers.add(MarkerData.createEventMarker(
                    klineDataList.get(10).getDate(),
                    "E"  // 单个字母
            ));

            // 4. 警告标记
            markers.add(MarkerData.createWarningMarker(
                    klineDataList.get(20).getDate(),
                    "!"  // 符号
            ));

            // 5. 信息标记
            markers.add(MarkerData.createInfoMarker(
                    klineDataList.get(25).getDate(),
                    "i"  // 单个字母
            ));

            // 6. 止损标记
            markers.add(MarkerData.createStopLossMarker(
                    klineDataList.get(40).getDate(),
                    "L"  // 单个字母
            ));

            // 7. 止盈标记
            markers.add(MarkerData.createTakeProfitMarker(
                    klineDataList.get(45).getDate(),
                    "P"  // 单个字母
            ));

            // 8. 纯文字标记 - 支持多个汉字
            markers.add(MarkerData.createTextMarker(
                    klineDataList.get(12).getDate(),
                    "重要消息"  // 多个汉字演示
            ));

            // 9. 圆点标记
            markers.add(MarkerData.createDotMarker(klineDataList.get(22).getDate()));

            // ========== 多汉字纯文字标记演示 ==========

            // 添加更多多汉字文字标记示例
            markers.add(new MarkerData(
                    klineDataList.get(7).getDate(),
                    "业绩公告",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(0xFF1976D2)
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.MEDIUM)
                            .build()
            ));

            markers.add(new MarkerData(
                    klineDataList.get(36).getDate(),
                    "分红除权",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(0xFF388E3C)
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.LONG)
                            .build()
            ));

            markers.add(new MarkerData(
                    klineDataList.get(44).getDate(),
                    "主力资金流入",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(0xFFD32F2F)
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.SHORT)
                            .build()
            ));

            // ========== 自定义标记样式 ==========

            // 10. 五角星标记
            markers.add(new MarkerData(
                    klineDataList.get(18).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.STAR)
                            .backgroundColor(0xFFFFD700)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.MEDIUM)
                            .build()
            ));

            // 11. 十字标记
            markers.add(new MarkerData(
                    klineDataList.get(28).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.CROSS)
                            .backgroundColor(0xFF607D8B)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.MEDIUM)
                            .build()
            ));

            // 12. 箭头向上
            markers.add(new MarkerData(
                    klineDataList.get(32).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.ARROW_UP)
                            .backgroundColor(0xFF8BC34A)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.SHORT)
                            .build()
            ));

            // 13. 箭头向下
            markers.add(new MarkerData(
                    klineDataList.get(38).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.ARROW_DOWN)
                            .backgroundColor(0xFFE91E63)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.SHORT)
                            .build()
            ));

            // 14. 菱形标记
            markers.add(new MarkerData(
                    klineDataList.get(30).getDate(),
                    "D",  // 单个字母
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.DIAMOND)
                            .backgroundColor(0xFF9C27B0)
                            .textColor(0xFFFFFFFF)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .textSize(8f) // 调整文字尺寸 (从6f改为8f)
                            .lineLength(LineLength.SHORT)
                            .build()
            ));

            // 15. 三角形向上
            markers.add(new MarkerData(
                    klineDataList.get(35).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.TRIANGLE_UP)
                            .backgroundColor(0xFF00C853)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.NONE)
                            .build()
            ));

            // 16. 三角形向下
            markers.add(new MarkerData(
                    klineDataList.get(42).getDate(),
                    "",
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.TRIANGLE_DOWN)
                            .backgroundColor(0xFFFF1744)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .showText(false)
                            .lineLength(LineLength.NONE)
                            .build()
            ));

            // ========== 不同线条长度演示 ==========

            // 17. 无虚线示例
            markers.add(new MarkerData(
                    klineDataList.get(2).getDate(),
                    "N",  // 单个字母
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.RECTANGLE)
                            .backgroundColor(0xFF4CAF50)
                            .textColor(0xFFFFFFFF)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .textSize(8f) // 调整文字尺寸 (从6f改为8f)
                            .showLine(false)
                            .build()
            ));

            // 18. 超长虚线示例
            markers.add(new MarkerData(
                    klineDataList.get(47).getDate(),
                    "X",  // 单个字母
                    new com.alex.klinemarker.data.MarkerConfig.Builder()
                            .shape(MarkerShape.CIRCLE)
                            .backgroundColor(0xFF2196F3)
                            .textColor(0xFFFFFFFF)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .textSize(8f) // 调整文字尺寸 (从6f改为8f)
                            .lineLength(LineLength.EXTRA_LONG)
                            .build()
            ));

            // ========== 基于预设的自定义 ==========

            // 19. 自定义买入标记
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(8).getDate(),
                    "C",  // 单个字母
                    MarkerPresets.customize(MarkerPresets.buy())
                            .backgroundColor(0xFF00E676)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .textSize(8f) // 调整文字尺寸 (从6f改为8f)
                            .build()
            ));

            // 20. 自定义警告标记
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(48).getDate(),
                    "W",  // 单个字母
                    MarkerPresets.customize(MarkerPresets.warning())
                            .position(MarkerPosition.BELOW)
                            .dashedLine(false)
                            .lineLength(LineLength.LONG)
                            .markerSize(12f) // 调整尺寸 (从8f改为12f)
                            .textSize(8f) // 调整文字尺寸 (从6f改为8f)
                            .build()
            ));
        }

        markerManager.setMarkers(markers);
        markerManager.refresh();

        Toast.makeText(getContext(), "已添加所有类型标记演示（共" + markers.size() + "个）", Toast.LENGTH_LONG).show();
    }

    private void addSampleTrendRegions() {
        List<TrendRegion> trendRegions = new ArrayList<>();

        if (klineDataList != null && !klineDataList.isEmpty()) {
            SimpleDateFormat trendDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // 上涨趋势区间（第5-20天）
            if (klineDataList.size() > 20) {
                TrendRegion risingTrend = new TrendRegion();
                risingTrend.setStart(trendDateFormat.format(klineDataList.get(5).getDate()));
                risingTrend.setEnd(trendDateFormat.format(klineDataList.get(20).getDate()));
                risingTrend.setType(TrendRegion.TrendType.RISING);
                risingTrend.setSize(5);
                trendRegions.add(risingTrend);
            }

            // 下跌趋势区间（第40-60天）
            if (klineDataList.size() > 60) {
                TrendRegion fallingTrend = new TrendRegion();
                fallingTrend.setStart(trendDateFormat.format(klineDataList.get(40).getDate()));
                fallingTrend.setEnd(trendDateFormat.format(klineDataList.get(60).getDate()));
                fallingTrend.setType(TrendRegion.TrendType.FALLING);
                fallingTrend.setSize(3);
                trendRegions.add(fallingTrend);
            }

            // 中性趋势区间（第75-85天）
            if (klineDataList.size() > 85) {
                TrendRegion neutralTrend = new TrendRegion();
                neutralTrend.setStart(trendDateFormat.format(klineDataList.get(75).getDate()));
                neutralTrend.setEnd(trendDateFormat.format(klineDataList.get(85).getDate()));
                neutralTrend.setType(TrendRegion.TrendType.NEUTRAL);
                neutralTrend.setSize(2);
                trendRegions.add(neutralTrend);
            }
        }

        markerManager.setTrendRegions(trendRegions);
        markerManager.refresh();

        Toast.makeText(getContext(), "已添加趋势区间背景", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}