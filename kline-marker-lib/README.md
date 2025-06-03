# K线标记库 (KLine Marker Library)

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

一个专为 MPAndroidChart 设计的 K线图标记和趋势区间显示库，让您轻松在K线图上添加专业级的标记和趋势背景效果。

## 📸 效果展示

- **多种标记类型**：买入🟢、卖出🔴、数字⚪、上三角🔺、下三角🔻
- **趋势区间背景**：上涨🟢、下跌🔴、中性🔵趋势的精美渐变阴影
- **自适应定位**：标记自动避免超出图表边界，虚线长度动态调整
- **高性能渲染**：只绘制可见区域，支持大量数据流畅滚动

## 🚀 5分钟快速上手

### 完整导入清单

首先，在您的Java文件中添加以下导入语句：

```java
// 核心类

import com.alex.klinemarker.KLineMarkerManager;
import com.alex.klinemarker.data.KLineDataAdapter;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerPresets;
import com.alex.klinemarker.data.TrendRegion;
import com.alex.klinemarker.data.MarkerConfig;
import com.alex.klinemarker.data.MarkerShape;
import com.alex.klinemarker.data.MarkerPosition;
import com.alex.klinemarker.data.LineLength;
import com.alex.klinemarker.core.TrendRegionConfig;

// MPAndroidChart
import com.github.mikephil.charting.charts.CombinedChart;

// Android 标准库
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
```

### 第1步：添加库到项目

1. 将 `kline-marker-lib` 文件夹复制到您的项目根目录
2. 在 `settings.gradle.kts` 文件中添加一行：
   ```kotlin
   include(":kline-marker-lib")
   ```
3. 在 `app/build.gradle.kts` 的 dependencies 中添加：
   ```kotlin
   dependencies {
       implementation(project(":kline-marker-lib"))
       implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
   }
   ```

### 第2步：创建数据适配器

告诉库如何从您的K线数据中读取信息：

```java
import com.alex.klinemarker.data.KLineDataAdapter;

import java.util.Date;

public class MyKLineDataAdapter implements KLineDataAdapter<MyKLineData> {

   @Override
   public Date getDate(MyKLineData data) {
      return data.date; // 您K线数据的日期字段
   }

   @Override
   public float getHigh(MyKLineData data) {
      return data.high; // 最高价
   }

   @Override
   public float getLow(MyKLineData data) {
      return data.low;  // 最低价
   }

   @Override
   public float getOpen(MyKLineData data) {
      return data.open; // 开盘价
   }

   @Override
   public float getClose(MyKLineData data) {
      return data.close; // 收盘价
   }

   @Override
   public float getVolume(MyKLineData data) {
      return data.volume; // 成交量
   }

   @Override
   public float getXValue(MyKLineData data) {
      return data.index; // X轴位置，通常是数组索引
   }
}
```

### 第3步：初始化标记管理器

在您的Activity或Fragment中：

```java
import com.alex.klinemarker.KLineMarkerManager;
import com.github.mikephil.charting.charts.CombinedChart;

import java.util.List;

public class MainActivity extends AppCompatActivity {
   private KLineMarkerManager<MyKLineData> markerManager;
   private CombinedChart chart;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      chart = findViewById(R.id.chart); // 您的图表

      // 创建适配器
      MyKLineDataAdapter adapter = new MyKLineDataAdapter();

      // 创建管理器
      markerManager = new KLineMarkerManager.Builder<MyKLineData>()
                .context(this)
              .chart(chart)
                .dataAdapter(adapter)
                .build();

      // 设置K线数据
      List<MyKLineData> klineData = getYourKLineData();
      markerManager.setKLineData(klineData);
   }
}
```

### 第4步：添加标记

```java
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerPresets;

import java.util.ArrayList;
import java.util.Date;

// 创建标记列表
List<MarkerData> markers = new ArrayList<>();

        // 添加买入标记
        Date buyDate = new Date(); // 买入的日期
markers.

        add(MarkerData.createCustomMarker(buyDate, "买入",MarkerPresets.buy()));

        // 添加卖出标记
        Date sellDate = new Date(); // 卖出的日期  
markers.

        add(MarkerData.createCustomMarker(sellDate, "卖出",MarkerPresets.sell()));

// 显示标记
        markerManager.

        setMarkers(markers);
markerManager.

        refresh();
```

### 第5步：添加趋势区间（可选）

```java
import com.alex.klinemarker.data.TrendRegion;

// 创建趋势区间列表
List<TrendRegion> trendRegions = new ArrayList<>();

// 添加上涨趋势区间
trendRegions.

        add(new TrendRegion(
                    "2024-01-15",           // 开始日期
    "2024-01-25",           // 结束日期  
                    3,                      // 趋势强度（1-5）
                    "2024-01-26T10:00:00Z", // 创建时间
            TrendRegion.TrendType.RISING  // 趋势类型：上涨
        ));

// 添加下跌趋势区间
        trendRegions.

        add(new TrendRegion(
                    "2024-02-01", "2024-02-10",4,
                    "2024-02-11T14:30:00Z",
            TrendRegion.TrendType.FALLING  // 趋势类型：下跌
        ));

// 显示趋势区间
        markerManager.

        setTrendRegions(trendRegions);
markerManager.

        refresh();
```

就这么简单！🎉

## 🎨 常用功能

### 不同类型的标记

```java
List<MarkerData> markers = new ArrayList<>();

// 买入标记（绿色圆角矩形）
markers.

add(MarkerData.createCustomMarker(date, "买",MarkerPresets.buy()));

// 卖出标记（红色圆角矩形）
        markers.

add(MarkerData.createCustomMarker(date, "卖",MarkerPresets.sell()));

// 信息标记（蓝色圆形）
        markers.

add(MarkerData.createCustomMarker(date, "消息",MarkerPresets.info()));

// 重要标记（黄色星形）
        markers.

add(MarkerData.createCustomMarker(date, "重要",MarkerPresets.important()));

// 警告标记（红色圆形）
        markers.

add(MarkerData.createCustomMarker(date, "警告",MarkerPresets.warning()));

// 事件标记（紫色菱形）
        markers.

add(MarkerData.createCustomMarker(date, "事件",MarkerPresets.event()));
```

### 预设标记类型

| 预设方法                        | 外观   | 用途   | 颜色   |
|-----------------------------|------|------|------|
| `MarkerPresets.buy()`       | 🟢矩形 | 买入信号 | 谷歌绿色 |
| `MarkerPresets.sell()`      | 🔴矩形 | 卖出信号 | 谷歌红色 |
| `MarkerPresets.info()`      | 🔵圆形 | 信息提示 | 谷歌蓝色 |
| `MarkerPresets.warning()`   | 🟡圆形 | 警告信息 | 谷歌红色 |
| `MarkerPresets.important()` | ⭐星形  | 重要事件 | 谷歌黄色 |
| `MarkerPresets.event()`     | 💎菱形 | 事件标记 | 菱形紫色 |
| `MarkerPresets.success()`   | 🟢圆形 | 成功信息 | 谷歌绿色 |
| `MarkerPresets.error()`     | 🔴圆形 | 错误信息 | 谷歌红色 |

## 🎨 自定义配置

### 标记外观配置

```java
// 创建自定义配置
MarkerConfig markerConfig = new MarkerConfig.Builder()
                .markerSize(16f)                        // 标记大小(dp)
                .textSize(12f)                          // 文字大小(sp) 
                .backgroundColor(Color.parseColor("#4CAF50"))  // 背景颜色
                .textColor(Color.WHITE)                 // 文字颜色
                .lineColor(Color.parseColor("#4CAF50")) // 虚线颜色
                .lineLength(LineLength.MEDIUM)          // 虚线长度：SHORT/MEDIUM/LONG
                .position(MarkerPosition.AUTO)          // 位置：AUTO/ABOVE/BELOW
                .shape(MarkerShape.CIRCLE)              // 形状：CIRCLE/RECTANGLE/TRIANGLE_UP/TRIANGLE_DOWN
                .showText(true)                         // 是否显示文字
                .showLine(true)                         // 是否显示虚线
                .build();

// 使用自定义配置创建标记管理器
markerManager =new KLineMarkerManager.Builder<MyKLineData>()
        .

context(this)
        .

chart(chart) 
        .

dataAdapter(adapter)
        .

markerConfig(markerConfig)  // 应用自定义配置
        .

build();
```

### 趋势区间配置

```java
import com.alex.klinemarker.core.TrendRegionConfig;

import android.graphics.Color;

TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
        .risingColor(Color.parseColor("#4CAF50"))    // 上涨趋势颜色
        .fallingColor(Color.parseColor("#F44336"))   // 下跌趋势颜色
        .neutralColor(Color.parseColor("#2196F3"))   // 中性趋势颜色
        .topAlpha(0.3f)                              // 顶部透明度
        .bottomAlpha(0.1f)                           // 底部透明度
        .offsetDp(4f)                                // 偏移距离
        .enableBezierCurve(true)                     // 启用贝塞尔曲线平滑
        .enableGradient(true)                        // 启用渐变效果
        .enablePerformanceMode(true)                 // 启用性能模式
        .maxVisibleRegions(10)                       // 最大可见区间数量
        .build();

// 应用趋势区间配置
markerManager =new KLineMarkerManager.Builder<MyKLineData>()
        .

context(this)
        .

chart(chart)
        .

dataAdapter(adapter)
        .

trendRegionConfig(trendConfig)  // 应用趋势区间配置
        .

build();
```

## 📈 趋势区间类型

### 趋势类型说明

- **RISING** 🟢：上涨趋势，绿色渐变背景，沿K线高点自然延伸
- **FALLING** 🔴：下跌趋势，红色渐变背景，沿K线低点自然延伸
- **NEUTRAL** 🔵：中性/震荡趋势，蓝色渐变背景，统一样式

### 从JSON数据加载趋势区间

```java
// JSON格式示例
String jsonData = """
                [
                  {
                    "startDate": "2024-01-15",
                    "endDate": "2024-01-25", 
                    "intensity": 3,
                    "createTime": "2024-01-26T10:00:00Z",
                    "trendType": "RISING"
                  },
                  {
                    "startDate": "2024-02-01",
                    "endDate": "2024-02-10",
                    "intensity": 4, 
                    "createTime": "2024-02-11T14:30:00Z",
                    "trendType": "FALLING"
                  }
                ]
                """;

// 从JSON加载趋势区间
markerManager.

setTrendRegionsFromJson(jsonData);
markerManager.

refresh();
```

## 🎯 高级用法

### 动态管理标记

```java
// 清除所有标记
markerManager.clearMarkers();

// 清除所有趋势区间  
markerManager.

clearTrendRegions();

// 清除所有内容
markerManager.

clearAll();

// 动态添加新标记
List<MarkerData> newMarkers = createNewMarkers();
markerManager.

setMarkers(newMarkers);
markerManager.

refresh();
```

### 自定义标记

```java
// 使用颜色预设创建自定义标记
MarkerData customMarker = MarkerData.createCustomMarker(
                date,
                "自定义文字",
                MarkerPresets.googleBlue()  // 使用谷歌蓝色预设
        );

// 基于预设进行深度自定义
MarkerData advancedMarker = MarkerData.createCustomMarker(
        date,
        "⚡",
        MarkerPresets.customize(MarkerPresets.googleYellow())
                .shape(MarkerShape.TRIANGLE_UP)
                .markerSize(18f)
                .showLine(false)
                .build()
);
```

### 性能优化建议

```java
// 1. 启用性能模式（推荐用于大数据量场景）
TrendRegionConfig performanceConfig = new TrendRegionConfig.Builder()
                .enablePerformanceMode(true)     // 启用性能模式
                .maxVisibleRegions(5)            // 限制可见区间数量
                .topAlpha(0.15f)                 // 降低透明度减少渲染负担
                .bottomAlpha(0.05f)
                .build();

// 2. 合理设置可见范围
chart.

setVisibleXRangeMaximum(50f);  // 最大显示50个K线
chart.

setVisibleXRangeMinimum(10f);  // 最小显示10个K线

// 3. 避免频繁刷新
// 批量更新后统一刷新，而不是每次更新都刷新
markerManager.

setKLineData(klineData);
markerManager.

setMarkers(markers);
markerManager.

setTrendRegions(trendRegions);
markerManager.

refresh(); // 统一刷新一次
```

### 自定义数据适配器示例

```java
// 适配自定义K线数据结构
public class CustomKLineAdapter implements KLineDataAdapter<CustomKLineBean> {

   @Override
   public Date getDate(CustomKLineBean data) {
      // 如果您的数据使用时间戳
      return new Date(data.timestamp * 1000L);
   }

   @Override
   public float getXValue(CustomKLineBean data) {
      // 使用索引作为X坐标
      return data.index;

      // 或者使用相对天数（更推荐）
      // long baseTime = 1704067200000L; // 2024-01-01基准时间
      // return (data.timestamp * 1000L - baseTime) / (24 * 60 * 60 * 1000f);
   }

   @Override
   public float getOpen(CustomKLineBean data) {
      return Float.parseFloat(data.openPrice);
   }

   // ... 其他方法类似实现
}
```

## 🎨 颜色预设系统

为了避免杂乱的颜色使用，我们引入了统一的颜色预设系统：

### 谷歌Logo四色

- **谷歌蓝** (`0xFF4285F4`) - 信息、提示类标记
- **谷歌红** (`0xFFEA4335`) - 警告、错误、卖出类标记
- **谷歌绿** (`0xFF34A853`) - 成功、买入类标记
- **谷歌黄** (`0xFFFBBC04`) - 重要、关注类标记

### 股票经典色彩

- **股票红** (`0xFFFF4444`) - 中国股市下跌色
- **股票绿** (`0xFF00AA00`) - 中国股市上涨色

### 经典紫色

- **菱形紫** (`0xFF9C27B0`) - 经典的菱形标记紫色

### 使用示例

```java
// 使用预设标记
MarkerData.createCustomMarker(date, "B",MarkerPresets.buy());        // 买入标记
        MarkerData.

createCustomMarker(date, "S",MarkerPresets.sell());       // 卖出标记
        MarkerData.

createCustomMarker(date, "i",MarkerPresets.info());       // 信息标记
        MarkerData.

createCustomMarker(date, "★",MarkerPresets.important());  // 重要标记

// 圆形标记大小一致性演示 - 不同字符相同大小
MarkerData chinese = MarkerData.createCustomMarker(date, "买", MarkerPresets.googleBlue());   // 中文字符，16dp圆形
MarkerData english = MarkerData.createCustomMarker(date, "B", MarkerPresets.googleGreen());   // 英文字符，16dp圆形
MarkerData number = MarkerData.createCustomMarker(date, "1", MarkerPresets.googleRed());      // 数字字符，16dp圆形
MarkerData symbol = MarkerData.createCustomMarker(date, "★", MarkerPresets.googleYellow());   // 符号字符，16dp圆形
// 以上所有圆形标记都是完全相同的大小，由markerSize统一控制（默认16dp）

// 直接使用颜色预设
MarkerData.

createCustomMarker(date, "蓝",MarkerPresets.googleBlue());
        MarkerData.

createCustomMarker(date, "涨",MarkerPresets.stockGreen());
        MarkerData.

createCustomMarker(date, "紫",MarkerPresets.diamondPurple());

// 基于预设进行自定义
        MarkerData.

createCustomMarker(date, "自定义",
                   MarkerPresets.customize(MarkerPresets.googleBlue())
        .

shape(MarkerShape.STAR)
        .

markerSize(14f)
        .

build());

// 使用颜色常量
        new MarkerConfig.

Builder()
    .

backgroundColor(MarkerColors.GOOGLE_BLUE)
    .

textColor(MarkerColors.BuyColors.TEXT)
    .

lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_BLUE))
        .

build();
```

## ❓ 常见问题

### Q: 标记没有显示出来？

A: 检查以下几点：

1. 确保调用了 `markerManager.refresh()`
2. 确保标记的日期与K线数据的日期能够匹配
3. 确保标记的日期在图表的可见范围内

### Q: 如何清除所有标记？

A: 使用 `markerManager.clearMarkers()` 或 `markerManager.clearAll()`

### Q: 如何动态添加新标记？

A: 创建新的标记列表，调用 `setMarkers()` 和 `refresh()`

### Q: 标记位置不对？

A: 检查适配器的 `getXValue()` 方法返回值是否与图表的X轴坐标一致

### Q: 日期格式要求？

```java
// ✅ 正确：使用Java Date对象
MarkerData marker = MarkerData.createCustomMarker(new Date(), "买入", MarkerPresets.buy());

// ❌ 错误：直接使用字符串（不支持）
// MarkerData marker = MarkerData.createCustomMarker("2024-01-15", "买入", MarkerPresets.buy());
```

### Q: X轴坐标一致性？

```java
// 确保适配器的getXValue()返回值与图表数据的X坐标一致
@Override
public float getXValue(MyKLineData data) {
   // 如果图表使用索引作为X坐标
   return data.index;

   // 如果图表使用相对天数作为X坐标
   // return calculateRelativeDays(data.date);
}
```

## 📚 完整示例

查看项目中的 `sample` 模块获取完整的使用示例，包含：

- 完整的K线图表配置
- 多种标记类型的演示
- 趋势区间的使用方法
- 自定义配置的应用
- 性能优化的最佳实践

## 🔧 技术要求

- **Android API**: 21+ (Android 5.0+)
- **MPAndroidChart**: v3.1.0+
- **Java**: 8+
- **Kotlin**: 1.8+ (可选)

## 📄 许可证

```
MIT License

Copyright (c) 2024 Alex

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🤝 贡献与反馈

欢迎提交 Issue 和 Pull Request 来改进这个库！如果您在使用过程中遇到问题，请：

1. 首先查看本文档是否有相关说明
2. 查看 `sample` 模块中的完整示例
3. 在 GitHub Issues 中描述您的问题和环境

---

⭐ **如果这个库对您有帮助，请给我们一个 Star！您的支持是我们持续改进的动力。** 