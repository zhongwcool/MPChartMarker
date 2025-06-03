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
import com.alex.klinemarker.core.TrendRegionConfig;
import com.alex.klinemarker.data.LineLength;
import com.alex.klinemarker.data.MarkerColors;
import com.alex.klinemarker.data.MarkerConfig;
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

        // 创建标记配置 - 使用新的data包MarkerConfig
        MarkerConfig markerConfig = new MarkerConfig.Builder()
                .markerSize(12f) // 调整标记大小
                .textSize(8f) // 相应调整文字大小
                .backgroundColor(Color.parseColor("#00AA00"))
                .textColor(Color.WHITE)
                .lineColor(Color.parseColor("#00AA00"))
                .showText(true)
                .showLine(true)
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

            // ========== 演示圆形标记大小一致性 ==========
            // 以下圆形标记虽然内容不同（中文、英文、数字），但大小完全一致

            // 17. 圆形 - 中文字符
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 50).getDate(),
                    "买", // 中文字符，圆形大小固定
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.CIRCLE)
                            .backgroundColor(MarkerColors.GOOGLE_BLUE)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 18. 圆形 - 英文字符（相同大小）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 49).getDate(),
                    "A", // 英文字符，圆形大小与中文一致
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.CIRCLE)
                            .backgroundColor(MarkerColors.GOOGLE_GREEN)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 19. 圆形 - 数字字符（相同大小）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 47).getDate(),
                    "1", // 数字字符，圆形大小与中文英文一致
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.CIRCLE)
                            .backgroundColor(MarkerColors.GOOGLE_RED)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 20. 圆形 - 符号字符（相同大小）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 46).getDate(),
                    "★", // 符号字符，圆形大小一致
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.CIRCLE)
                            .backgroundColor(MarkerColors.GOOGLE_YELLOW)
                            .textColor(0xFF000000)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // ========== 演示中文字符支持限制 ==========
            // 只有圆形和菱形支持中文字符显示，其他形状遇到中文会不显示文字

            // 1. 圆形标记 - 支持中文
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 45).getDate(),  // 倒数第45个
                    "买入", // 中文字符，圆形支持，会显示"买"
                    MarkerPresets.googleBlue()  // 使用预设，自动16dp
            ));

            // 2. 菱形标记 - 支持中文
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 40).getDate(),  // 倒数第40个
                    "卖出", // 中文字符，菱形支持，会显示"卖"
                    MarkerPresets.diamondPurple()  // 使用预设，自动16dp
            ));

            // 3. 矩形标记 - 不支持中文（文字被隐藏）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 35).getDate(),  // 倒数第35个
                    "警告", // 中文字符，矩形不支持，不显示文字
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.RECTANGLE)
                            .backgroundColor(MarkerColors.GOOGLE_RED)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp（虽然矩形是固定大小）
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 4. 三角形标记 - 不支持任何文字（纯图标）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 30).getDate(),  // 倒数第30个
                    "止损", // 任何文字都不会显示，纯三角形图标
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.TRIANGLE_UP)
                            .backgroundColor(MarkerColors.GOOGLE_GREEN)
                            .showText(true) // 即使设置为true也不会显示文字
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 5. 星形标记 - 不支持任何文字（纯图标）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 25).getDate(),  // 倒数第25个
                    "重要", // 任何文字都不会显示，纯星形图标
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.STAR)
                            .backgroundColor(MarkerColors.GOOGLE_YELLOW)
                            .showText(true) // 即使设置为true也不会显示文字
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // ========== 演示英文字符在所有形状中都正常显示 ==========

            // 6. 圆形 - 英文字符正常显示
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 42).getDate(),  // 倒数第42个
                    "BUY",  // 英文字符，会显示"B"
                    MarkerPresets.googleBlue()  // 使用预设，自动16dp
            ));

            // 7. 矩形 - 英文字符正常显示
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 37).getDate(),  // 倒数第37个
                    "SELL", // 英文字符，会显示"S"
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.RECTANGLE)
                            .backgroundColor(MarkerColors.GOOGLE_RED)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp（虽然矩形是固定大小）
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 8. 三角形 - 英文字符正常显示
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 32).getDate(),  // 倒数第32个
                    "UP",   // 英文字符，会显示"U"
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.TRIANGLE_UP)
                            .backgroundColor(MarkerColors.GOOGLE_GREEN)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // 9. 星形 - 英文字符正常显示
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 27).getDate(),  // 倒数第27个
                    "STAR", // 英文字符，会显示"S"
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.STAR)
                            .backgroundColor(MarkerColors.GOOGLE_YELLOW)
                            .textColor(0xFF000000)
                            .showText(true)
                            .showLine(true)
                            .markerSize(16f) // 统一16dp
                            .textSize(10f)   // 统一10f
                            .build()
            ));

            // ========== 演示数字和符号在所有形状中都正常显示 ==========

            // 10. 圆形 - 数字
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 20).getDate(),
                    "123",  // 数字，会显示"1"
                    MarkerPresets.googleBlue()  // 使用谷歌蓝色预设，自动16dp
            ));

            // 11. 矩形 - 符号
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 15).getDate(),
                    "★☆",  // 符号，会显示"★"
                    MarkerPresets.sell()  // 使用卖出预设（红色矩形），自动16dp
            ));

            // ========== TEXT ONLY 演示（不受中文限制影响）==========

            // 12-14. 纯文字标记 - 支持所有字符，包括中文多字符
            markers.add(new MarkerData(
                    klineDataList.get(dataSize - 43).getDate(),  // 倒数第43个
                    "中文业绩公告",  // TEXT ONLY 不限制中文
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(MarkerColors.GOOGLE_BLUE)
                            .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_BLUE))
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.MEDIUM)
                            .build()
            ));

            markers.add(new MarkerData(
                    klineDataList.get(dataSize - 14).getDate(),  // 倒数第14个
                    "主力资金流入",  // TEXT ONLY 不限制中文
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(MarkerColors.GOOGLE_GREEN)
                            .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_GREEN))
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.LONG)
                            .build()
            ));

            markers.add(new MarkerData(
                    klineDataList.get(dataSize - 6).getDate(),   // 倒数第6个
                    "English Mixed 中英混合",  // TEXT ONLY 支持中英混合
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.NONE)
                            .textColor(MarkerColors.GOOGLE_RED)
                            .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_RED))
                            .textSize(10f)
                            .showText(true)
                            .showLine(true)
                            .lineLength(LineLength.SHORT)
                            .build()
            ));

            // ========== 演示混合文字的处理 ==========

            // 15. 圆形 - 中英混合（支持中文）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 48).getDate(),
                    "A买", // 中英混合，圆形支持中文，会显示"A"
                    MarkerPresets.googleBlue()
            ));

            // 16. 矩形 - 中英混合（不支持中文，文字被隐藏）
            markers.add(MarkerData.createCustomMarker(
                    klineDataList.get(dataSize - 18).getDate(),
                    "B卖", // 中英混合，矩形不支持中文，不显示文字
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.RECTANGLE)
                            .backgroundColor(MarkerColors.STOCK_RED)
                            .textColor(0xFFFFFFFF)
                            .showText(true)
                            .showLine(true)
                            .markerSize(12f)
                            .textSize(8f)
                            .build()
            ));

            // ========== 保留原有标记以向后兼容 ==========

            // 26. 圆点标记 - 无文字
            markers.add(new MarkerData(
                    klineDataList.get(dataSize - 28).getDate(), // 倒数第28个
                    "",
                    new MarkerConfig.Builder()
                            .shape(MarkerShape.DOT)
                            .position(MarkerPosition.AUTO)
                            .backgroundColor(0xFF666666) // 中性灰色
                            .showText(false)
                            .showLine(true)
                            .lineColor(0xFF999999)
                            .lineLength(LineLength.NONE)  // 固定使用NONE：短距离且不显示虚线
                            .markerSize(12f)
                            .build()
            ));
        }

        markerManager.setMarkers(markers);
        markerManager.refresh();

        Toast.makeText(getContext(), "已添加标记功能演示（共" + markers.size() + "个标记）\n" +
                "演示内容：\n" +
                "• 所有标记统一16dp大小：确保\"买\"、\"A\"、\"1\"等标记完全一致\n" +
                "• 中文字符限制：只有圆形和菱形支持中文\n" +
                "• 矩形：支持文字，固定16dp直角正方形\n" +
                "• 三角形和星形：不支持文字显示（纯图标）\n" +
                "• TEXT ONLY：不受限制，支持多字符", Toast.LENGTH_LONG).show();
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