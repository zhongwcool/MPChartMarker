# MPChart K线标记库

一个专为MPAndroidChart设计的K线标记扩展库，可以在K线图上添加各种类型的标记和趋势区间背景，包括买入/卖出标记、数字标记、三角形标记、趋势区间阴影等。

## 📦 安装

### 方式一：通过 JitPack（推荐）

1. 在项目级 `build.gradle.kts` 中添加 JitPack 仓库：

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

2. 在 app 模块的 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.github.zhongwcool:MPChartMarker:v1.0.0")
}
```

### 方式二：本地 Maven 仓库（开发测试）

```kotlin
repositories {
    google()
    mavenCentral()
    mavenLocal() // 添加本地 Maven 仓库
}

dependencies {
    implementation("com.alex.klinemarker:kline-marker-lib:1.0.0")
}
```

### 方式三：作为模块引入

```kotlin
// settings.gradle.kts
include(":kline-marker-lib")

// app/build.gradle.kts
dependencies {
    implementation(project(":kline-marker-lib"))
}
```

## 🚀 如何使用这个库

### 最简单的使用方法（推荐新手查看）

1. **查看库的详细README**：[kline-marker-lib/README.md](kline-marker-lib/README.md) - 完整的使用指南
2. **参考完整示例**：查看 `sample/` 目录中的完整使用示例

### 3分钟快速上手

```java
// 1. 创建数据适配器（告诉库如何读取您的K线数据）
MyAdapter adapter = new MyAdapter();

// 2. 初始化标记管理器
KLineMarkerManager<MyKLineData> markerManager = 
    new KLineMarkerManager.Builder<MyKLineData>()
        .context(this)
        .chart(chart)  // 您的MPAndroidChart图表
        .dataAdapter(adapter)
        .build();

// 3. 设置K线数据
markerManager.setKLineData(klineDataList);

// 4. 添加标记
List<MarkerData> markers = new ArrayList<>();
markers.add(MarkerData.createCustomMarker(date, "买入", MarkerPresets.buy()));
markers.add(MarkerData.createCustomMarker(date, "卖出", MarkerPresets.sell()));
markerManager.setMarkers(markers);
markerManager.refresh();
```

就这么简单！🎉

## 项目结构

```
MPChartMarker/
├── app/                    # 示例应用
├── kline-marker-lib/       # K线标记库（核心库）
│   ├── src/main/java/com/alex/klinemarker/
│   │   ├── core/          # 核心渲染和配置类
│   │   ├── data/          # 数据模型和适配器
│   │   ├── utils/         # 工具类
│   │   └── KLineMarkerManager.java  # 主要入口类
│   ├── README.md          # 库详细说明文档
│   └── build.gradle.kts   # 库构建配置
├── sample/                # 完整使用示例
└── README.md              # 本文件（项目总览）
```

## 特性

- 🎯 **多种标记类型**：买入、卖出、数字、上三角、下三角
- 📊 **趋势区间背景**：上涨、下跌、中性趋势的精美阴影效果
- 🎨 **完全可自定义**：颜色、大小、字体、位置、透明度等都可配置
- 🔧 **灵活适配**：通过适配器模式支持任意K线数据格式
- 📱 **自动适配**：自动适配屏幕密度和安全区域
- ⚡ **高性能**：只绘制可见区域的标记和趋势区间，支持大量数据
- 🏗️ **易于使用**：Builder模式，链式调用，集成简单
- 📈 **JSON支持**：支持JSON格式的趋势区间数据导入
- 🎭 **平滑效果**：支持贝塞尔曲线和渐变效果
- 🔄 **大小一致性**：圆形标记大小统一，不受文字内容影响
- 🌏 **多语言优化**：特别优化汉字居中显示，支持中英文混合
- 🎨 **颜色预设系统**：统一的颜色预设，避免杂乱的颜色使用

## 快速开始

### 1. 添加依赖

在您的项目中添加库模块：

```kotlin
// settings.gradle.kts
include(":kline-marker-lib")

// app/build.gradle.kts
dependencies {
    implementation(project(":kline-marker-lib"))
}
```

### 2. 基本使用

```java
// 创建数据适配器
KLineDataAdapter<YourKLineData> adapter = new YourKLineDataAdapter();

// 创建标记管理器
KLineMarkerManager<YourKLineData> markerManager =
        new KLineMarkerManager.Builder<YourKLineData>()
                .context(this)
                .chart(combinedChart)
                .dataAdapter(adapter)
                .build();

// 设置K线数据
markerManager.setKLineData(klineDataList);

// 创建标记
List<MarkerData> markers = new ArrayList<>();
markers.add(MarkerData.createCustomMarker(new Date(), "买入", MarkerPresets.buy()));
markers.add(MarkerData.createCustomMarker(new Date(), "卖出", MarkerPresets.sell()));

// 显示标记
markerManager.setMarkers(markers);

// 创建趋势区间
List<TrendRegion> trendRegions = new ArrayList<>();
trendRegions.add(
        new TrendRegion("2024-01-15", "2024-01-25", 3,
        "2024-01-26T10:00:00Z", TrendRegion.TrendType.RISING));

// 显示趋势区间
markerManager.setTrendRegions(trendRegions);
markerManager.refresh();
```

## 核心组件

### 1. KLineMarkerManager

主要入口类，提供简单易用的API来管理K线标记。

### 2. MarkerData

标记数据模型，定义标记的类型、位置、文本等属性。

### 3. TrendRegion

趋势区间数据模型，定义趋势区间的时间范围、类型等属性。

### 4. KLineDataAdapter

数据适配器接口，用于适配不同格式的K线数据。

### 5. MarkerConfig

标记配置类，用于自定义标记的外观和行为。

### 6. TrendRegionConfig

趋势区间配置类，用于自定义趋势区间的外观和行为。

### 7. KLineMarkerRenderer

核心标记渲染器，负责在画布上绘制标记。

### 8. TrendRegionRenderer

核心趋势区间渲染器，负责在画布上绘制趋势区间背景。

## 标记类型

| 类型            | 说明   | 位置     | 外观      |
|---------------|------|--------|---------|
| BUY           | 买入标记 | K线低点下方 | 绿色圆角矩形  |
| SELL          | 卖出标记 | K线高点上方 | 红色圆角矩形  |
| NUMBER        | 数字标记 | K线中部   | 带引出线的文字 |
| UP_TRIANGLE   | 上三角  | K线高点上方 | 橙红色三角形  |
| DOWN_TRIANGLE | 下三角  | K线低点下方 | 紫色三角形   |

## 自定义配置

### 标记配置

```java
MarkerConfig markerConfig = new MarkerConfig.Builder()
        .markerSize(14f)                    // 标记大小
        .textSize(10f)                      // 文字大小
        .buyColor(Color.GREEN)              // 买入标记颜色
        .sellColor(Color.RED)               // 卖出标记颜色
        .fixedLineLength(30f)               // 虚线长度
        .markerOffsetMultiplier(2.0f)       // 标记偏移倍数
        .build();
```

### 趋势区间配置

```java
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
        .risingColor(Color.parseColor("#4CAF50"))    // 上涨趋势颜色
        .fallingColor(Color.parseColor("#F44336"))   // 下跌趋势颜色
        .topAlpha(0.3f)                              // 顶部透明度
        .bottomAlpha(0.1f)                           // 底部透明度
        .enableBezierCurve(true)                     // 启用贝塞尔曲线
        .enableGradient(true)                        // 启用渐变
        .build();
```

## 📚 文档

### 🚀 快速开始

- [快速开始指南](kline-marker-lib/docs/快速开始指南.md) - 5分钟快速集成和验证

### 📖 完整使用指南

- [K线标记库使用指南](kline-marker-lib/docs/K线标记库使用指南.md) - 完整的集成、配置和使用文档
- [颜色预设系统使用指南](kline-marker-lib/docs/颜色预设系统使用指南.md) - 新的统一颜色预设系统

### 🔧 深度解析

- [架构设计文档](kline-marker-lib/docs/架构设计文档.md) - 深入了解库的设计思路和实现原理
- [性能优化指南](kline-marker-lib/docs/性能优化指南.md) - 大数据量下的性能优化策略
- [自定义渲染器开发指南](kline-marker-lib/docs/自定义渲染器开发指南.md) - 开发自定义标记渲染器

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
MarkerData.createCustomMarker(date, "B", MarkerPresets.buy());        // 买入标记
MarkerData.createCustomMarker(date, "S", MarkerPresets.sell());       // 卖出标记
MarkerData.createCustomMarker(date, "i", MarkerPresets.info());       // 信息标记
MarkerData.createCustomMarker(date, "★", MarkerPresets.important());  // 重要标记

// 圆形标记大小一致性演示 - 不同字符相同大小
MarkerData chinese = MarkerData.createCustomMarker(date, "买", MarkerPresets.googleBlue());   // 中文字符，16dp圆形
MarkerData english = MarkerData.createCustomMarker(date, "B", MarkerPresets.googleGreen());   // 英文字符，16dp圆形
MarkerData number = MarkerData.createCustomMarker(date, "1", MarkerPresets.googleRed());      // 数字字符，16dp圆形
MarkerData symbol = MarkerData.createCustomMarker(date, "★", MarkerPresets.googleYellow());   // 符号字符，16dp圆形
// 以上所有圆形标记都是完全相同的大小，由markerSize统一控制（默认16dp）

// 直接使用颜色预设
MarkerData.createCustomMarker(date, "蓝", MarkerPresets.googleBlue());
MarkerData.createCustomMarker(date, "涨", MarkerPresets.stockGreen());
MarkerData.createCustomMarker(date, "紫", MarkerPresets.diamondPurple());

// 基于预设进行自定义
MarkerData.createCustomMarker(date, "自定义",
    MarkerPresets.customize(MarkerPresets.googleBlue())
        .shape(MarkerShape.STAR)
        .markerSize(14f)
        .build());

// 使用颜色常量
new MarkerConfig.Builder()
    .backgroundColor(MarkerColors.GOOGLE_BLUE)
    .textColor(MarkerColors.BuyColors.TEXT)
    .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_BLUE))
    .build();
```

## 示例应用

项目中的`app`模块包含了完整的使用示例，展示了：

- 各种标记类型的使用
- 自定义配置的应用
- 动态添加和删除标记
- 与网络API的集成

## 技术要求

- Android API 21+
- MPAndroidChart v3.1.0+
- Java 8+

## 架构设计

库采用了以下设计模式和原则：

1. **适配器模式**：通过`KLineDataAdapter`适配不同的K线数据格式
2. **Builder模式**：提供链式调用的配置方式
3. **策略模式**：不同类型的标记使用不同的绘制策略
4. **单一职责原则**：每个类都有明确的职责
5. **开闭原则**：易于扩展新的标记类型

## 性能优化

- 只绘制可见区域的标记
- 使用对象池减少内存分配
- 缓存绘制相关的计算结果
- 异步处理大量标记数据

## 贡献

欢迎提交Issue和Pull Request来改进这个库。

## 许可证

本项目基于MIT许可证开源。

## 更新日志

### v1.0.0

- 初始版本发布
- 支持5种基本标记类型
- 完整的配置系统
- 高性能渲染引擎
