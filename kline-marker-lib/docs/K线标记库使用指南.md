# K线标记库完整使用指南

## 📋 概述

`kline-marker-lib` 是一个基于 MPAndroidChart 的 K线图标记和趋势区间显示库，支持在
K线图上添加各种类型的标记（买入、卖出、数字、三角形等）和趋势区间背景。

## 🎯 主要特性

- 🎯 支持多种标记类型：买入、卖出、数字、上三角、下三角
- 📊 支持趋势区间背景：上涨、下跌、中性趋势的精美阴影效果
- 🎨 完全可自定义的外观配置
- 🔧 灵活的数据适配器，支持任意K线数据格式
- 📱 自动适配屏幕密度和安全区域
- ⚡ 高性能渲染，只绘制可见区域的标记和趋势区间
- 🏗️ Builder模式，链式调用，使用简单
- 📈 支持JSON格式的趋势区间数据导入

## 🚀 集成指南

### 1. 添加库到项目

#### 方法一：直接拷贝模块（推荐）

1. 将整个 `kline-marker-lib` 文件夹拷贝到目标工程根目录
2. 在目标工程的 `settings.gradle.kts` 中添加：

```kotlin
include(":kline-marker-lib")
```

3. 在目标工程的根级 `build.gradle.kts` 中添加：

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false  // 添加这行
}
```

4. 在 `gradle/libs.versions.toml` 中添加：

```toml
[plugins]
android-library = { id = "com.android.library", version.ref = "agp" }
```

#### 方法二：发布为 AAR（适用于多项目共享）

在 `kline-marker-lib/build.gradle.kts` 中添加发布配置：

```kotlin
android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        release(MavenPublication) {
            from components.release
            groupId = 'com.alex'
            artifactId = 'kline-marker-lib'
            version = '1.0.0'
        }
    }
}
```

### 2. 添加依赖

在目标应用模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    // 添加库依赖
    implementation(project(":kline-marker-lib"))
    
    // 确保已经添加了MPAndroidChart依赖
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
```

## 📖 使用指南

### 1. 创建数据适配器

为您的K线数据创建适配器：

```java
public class MyKLineDataAdapter implements KLineDataAdapter<MyKLineData> {
    
    @Override
    public Date getDate(MyKLineData klineData) {
        return klineData.getDate();
    }
    
    @Override
    public float getOpen(MyKLineData klineData) {
        return klineData.getOpen();
    }
    
    @Override
    public float getClose(MyKLineData klineData) {
        return klineData.getClose();
    }
    
    @Override
    public float getHigh(MyKLineData klineData) {
        return klineData.getHigh();
    }
    
    @Override
    public float getLow(MyKLineData klineData) {
        return klineData.getLow();
    }
    
    @Override
    public float getVolume(MyKLineData klineData) {
        return klineData.getVolume();
    }
    
    @Override
    public float getXValue(MyKLineData klineData) {
        // 将日期转换为 X 轴坐标值
        if (klineData.getDate() == null) return 0;
        long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
        long daysSinceBase = (klineData.getDate().getTime() - baseTime) / (24 * 60 * 60 * 1000);
        return (float) daysSinceBase;
    }
}
```

### 2. 初始化标记管理器

```java
public class MainActivity extends AppCompatActivity {
    private CombinedChart chart;
    private KLineMarkerManager<MyKLineData> markerManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化图表
        chart = findViewById(R.id.chart);
        setupChart();
        
        // 创建标记配置（可选）
        MarkerConfig markerConfig = new MarkerConfig.Builder()
            .markerSize(14f)
            .textSize(10f)
            .buyColor(Color.parseColor("#4CAF50"))
            .sellColor(Color.parseColor("#F44336"))
            .numberColor(Color.parseColor("#80BDBDBD"))
            .upTriangleColor(Color.parseColor("#FF5722"))
            .downTriangleColor(Color.parseColor("#9C27B0"))
            .fixedLineLength(25f)
            .shortLineLength(8f)
            .markerOffsetMultiplier(1.8f)
            .build();
        
        // 创建趋势区间配置（可选）
        TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
            .risingColor(Color.parseColor("#4CAF50"))
            .fallingColor(Color.parseColor("#F44336"))
            .neutralColor(Color.parseColor("#2196F3"))
            .topAlpha(0.25f)
            .bottomAlpha(0.08f)
            .offsetDp(6f)
            .smoothWindowSize(3)
            .enableSmoothing(true)
            .enableGradient(true)
            .enableBezierCurve(true)
            .build();
        
        // 创建标记管理器
        markerManager = new KLineMarkerManager.Builder<MyKLineData>()
            .context(this)
            .chart(chart)
            .dataAdapter(new MyKLineDataAdapter())
            .markerConfig(markerConfig)
            .trendRegionConfig(trendConfig)
            .build();
        
        // 设置数据
        setupData();
    }
    
    private void setupChart() {
        // 基础图表配置
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setScaleEnabled(true);
        chart.setDragEnabled(true);
        
        // 配置 X 轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        
        // 配置 Y 轴
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);
    }
}
```

### 3. 设置数据并显示标记

```java
private void setupData() {
    // 准备 K线数据
    List<MyKLineData> klineDataList = generateKLineData();
    
    // 创建 MPAndroidChart 数据
    List<CandleEntry> candleEntries = new ArrayList<>();
    for (int i = 0; i < klineDataList.size(); i++) {
        MyKLineData data = klineDataList.get(i);
        candleEntries.add(new CandleEntry(i, data.getHigh(), data.getLow(), 
                                         data.getOpen(), data.getClose()));
    }
    
    CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "K线");
    candleDataSet.setColor(Color.rgb(80, 80, 80));
    candleDataSet.setShadowColor(Color.DKGRAY);
    candleDataSet.setShadowWidth(0.7f);
    candleDataSet.setDecreasingColor(Color.RED);
    candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
    candleDataSet.setIncreasingColor(Color.GREEN);
    candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
    candleDataSet.setNeutralColor(Color.BLUE);
    candleDataSet.setDrawValues(false);
    
    CandleData candleData = new CandleData(candleDataSet);
    CombinedData combinedData = new CombinedData();
    combinedData.setData(candleData);
    
    // 设置数据到图表和管理器
    chart.setData(combinedData);
    markerManager.setKLineData(klineDataList);
    
    // 创建标记数据
    List<MarkerData> markers = createSampleMarkers();
    markerManager.setMarkers(markers);
    
    // 创建趋势区间数据
    List<TrendRegion> trendRegions = createSampleTrendRegions();
    markerManager.setTrendRegions(trendRegions);
    
    // 刷新显示
    markerManager.refresh();
}

private List<MarkerData> createSampleMarkers() {
    List<MarkerData> markers = new ArrayList<>();
    
    // 添加买入标记
    MarkerData buyMarker = new MarkerData(
        new Date(), 
        MarkerData.MarkerType.BUY, 
        "买入"
    );
    markers.add(buyMarker);
    
    // 添加卖出标记
    MarkerData sellMarker = new MarkerData(
        new Date(), 
        MarkerData.MarkerType.SELL, 
        "卖出"
    );
    markers.add(sellMarker);
    
    // 添加数字标记
    MarkerData numberMarker = new MarkerData(
        new Date(), 
        MarkerData.MarkerType.NUMBER, 
        "1"
    );
    markers.add(numberMarker);
    
    return markers;
}

private List<TrendRegion> createSampleTrendRegions() {
    List<TrendRegion> trendRegions = new ArrayList<>();
    
    // 添加上涨趋势区间
    TrendRegion risingRegion = new TrendRegion(
        "2024-01-15", 
        "2024-01-25", 
        3, 
        "2024-01-26T10:00:00Z", 
        TrendRegion.TrendType.RISING
    );
    trendRegions.add(risingRegion);
    
    // 添加下跌趋势区间
    TrendRegion fallingRegion = new TrendRegion(
        "2024-02-01", 
        null, // null表示到最后
        2, 
        "2024-02-01T10:00:00Z", 
        TrendRegion.TrendType.FALLING
    );
    trendRegions.add(fallingRegion);
    
    return trendRegions;
}
```

## 🎨 配置选项

### 标记配置 (MarkerConfig)

```java
MarkerConfig config = new MarkerConfig.Builder()
    .markerSize(14f)                  // 标记大小
    .textSize(10f)                    // 文字大小
    .buyColor(Color.GREEN)            // 买入标记颜色
    .sellColor(Color.RED)             // 卖出标记颜色
    .numberColor(Color.GRAY)          // 数字标记颜色
    .upTriangleColor(Color.BLUE)      // 上三角标记颜色
    .downTriangleColor(Color.PURPLE)  // 下三角标记颜色
    .fixedLineLength(25f)             // 固定连接线长度
    .shortLineLength(8f)              // 短连接线长度
    .markerOffsetMultiplier(1.8f)     // 标记偏移倍数
    .build();
```

### 趋势区间配置 (TrendRegionConfig)

```java
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
    .risingColor(Color.parseColor("#4CAF50"))     // 上涨区间颜色
    .fallingColor(Color.parseColor("#F44336"))    // 下跌区间颜色
    .neutralColor(Color.parseColor("#2196F3"))    // 中性区间颜色
    .topAlpha(0.25f)                              // 顶部透明度
    .bottomAlpha(0.08f)                           // 底部透明度
    .offsetDp(6f)                                 // 偏移量
    .smoothWindowSize(3)                          // 平滑窗口大小
    .enableSmoothing(true)                        // 启用平滑
    .enableGradient(true)                         // 启用渐变
    .enableBezierCurve(true)                      // 启用贝塞尔曲线
    .build();
```

## 📊 标记类型详解

### 1. 买入标记 (BUY)

- 显示在K线低点下方
- 默认绿色圆角矩形
- 带虚线连接到K线

### 2. 卖出标记 (SELL)

- 显示在K线高点上方
- 默认红色圆角矩形
- 带虚线连接到K线

### 3. 数字标记 (NUMBER)

- 显示在K线中部
- 带引出线和文字
- 灰色背景，黑色文字

### 4. 上三角标记 (UP_TRIANGLE)

- 显示在K线低点下方
- 三角形指向上方
- 表示看涨信号

### 5. 下三角标记 (DOWN_TRIANGLE)

- 显示在K线高点上方
- 三角形指向下方
- 表示看跌信号

## 📈 趋势区间功能

### JSON数据格式

支持以下JSON格式的趋势区间数据：

```json
{
  "items": [
    {
      "updated_at": "2024-05-22T07:09:33.289230Z",
      "start": "2024-05-15",
      "end": null,
      "size": 2,
      "type": "RISING"
    },
    {
      "updated_at": "2024-05-22T06:46:10.313136Z",
      "start": "2024-03-19",
      "end": "2024-03-24",
      "size": 3,
      "type": "FALLING"
    }
  ]
}
```

### 从JSON设置趋势区间

```java
// 从JSON数据设置趋势区间
String jsonData = getJsonData(); // 获取JSON数据
markerManager.setTrendRegionsFromJson(jsonData);
```

### 趋势区间类型

- **RISING**: 上涨趋势区间（绿色渐变背景）
- **FALLING**: 下跌趋势区间（红色渐变背景）
- **NEUTRAL**: 中性趋势区间（蓝色渐变背景）

## 🔧 高级功能

### 自动趋势检测

库会自动检测连续的上涨或下跌K线，并生成对应的趋势区间：

- 连续3个或以上的上涨K线 → 上涨趋势区间
- 连续3个或以上的下跌K线 → 下跌趋势区间

### 性能优化

- 只绘制当前可见区域内的标记和趋势区间
- 使用缓存机制减少重复计算
- 异步数据处理，不阻塞UI线程

### 动态更新

```java
// 动态添加标记
markerManager.addMarker(newMarker);

// 动态移除标记
markerManager.removeMarker(markerId);

// 清空所有标记
markerManager.clearMarkers();

// 刷新显示
markerManager.refresh();
```

## 🎯 最佳实践

1. **数据预处理**: 在设置数据前进行预处理，确保数据格式正确
2. **性能考虑**: 对于大量数据，建议分批加载和显示
3. **用户体验**: 提供加载状态指示，避免长时间等待
4. **错误处理**: 添加适当的错误处理和日志记录
5. **内存管理**: 及时清理不需要的数据，避免内存泄漏

## 🔍 故障排除

### 常见问题

1. **标记不显示**: 检查数据适配器实现是否正确
2. **趋势区间错位**: 确认X轴坐标计算方法
3. **性能问题**: 减少数据量或优化绘制频率
4. **内存溢出**: 检查是否有循环引用或大对象未释放

### 调试技巧

```java
// 开启调试日志
markerManager.setDebugMode(true);

// 查看当前标记数量
Log.d("TAG", "Marker count: " + markerManager.getMarkerCount());

// 查看趋势区间数量
Log.d("TAG", "Trend region count: " + markerManager.getTrendRegionCount());
```

## 📄 API参考

### KLineMarkerManager

主要方法：

- `setKLineData(List<T> data)`: 设置K线数据
- `setMarkers(List<MarkerData> markers)`: 设置标记数据
- `setTrendRegions(List<TrendRegion> regions)`: 设置趋势区间
- `setTrendRegionsFromJson(String json)`: 从JSON设置趋势区间
- `refresh()`: 刷新显示
- `clear()`: 清空所有数据

### MarkerData

构造方法：

- `MarkerData(Date date, MarkerType type, String text)`

### TrendRegion

构造方法：

- `TrendRegion(String start, String end, int size, String updatedAt, TrendType type)`

这个库为MPAndroidChart提供了强大的标记和趋势区间功能，让您的K线图更加专业和直观！ 