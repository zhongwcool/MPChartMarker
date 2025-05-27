# K线标记库 (KLine Marker Library)

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

一个专为 MPAndroidChart 设计的 K线图标记和趋势区间显示库，提供专业级的图表标记功能。

## 📋 概述

`kline-marker-lib` 是一个基于 MPAndroidChart 的扩展库，支持在 K线图上添加各种类型的标记（买入、卖出、数字、三角形等）和精美的趋势区间背景。

## 🎯 主要特性

- 🎯 **多种标记类型**：买入、卖出、数字、上三角、下三角
- 📊 **趋势区间背景**：上涨、下跌、中性趋势的精美阴影效果
- 🎨 **完全可自定义**：外观配置、颜色、大小、位置等
- 🔧 **灵活适配器**：支持任意K线数据格式
- 📱 **自动适配**：屏幕密度和安全区域
- ⚡ **高性能渲染**：只绘制可见区域的标记和趋势区间
- 🏗️ **Builder模式**：链式调用，使用简单
- 📈 **JSON支持**：支持JSON格式的趋势区间数据导入

## 📚 文档目录

### 🚀 快速开始

- [快速开始指南](docs/快速开始指南.md) - 5分钟快速集成和验证

### 📖 完整使用指南

- [K线标记库使用指南](docs/K线标记库使用指南.md) - 完整的集成、配置和使用文档

### 🔧 深度解析

- [功能特性详解](docs/功能特性详解.md) - 技术实现细节、算法原理和高级定制

## 🎨 预览效果

### 标记类型展示

- **买入标记**：绿色圆角矩形，显示在K线低点下方
- **卖出标记**：红色圆角矩形，显示在K线高点上方
- **数字标记**：灰色圆形，带引出线指向K线
- **三角标记**：上三角（看涨）、下三角（看跌）

### 趋势区间效果

- **上涨区间**：绿色渐变背景，沿K线高点自然阴影
- **下跌区间**：红色渐变背景，沿K线低点自然阴影
- **中性区间**：蓝色渐变背景，统一样式

## 📦 安装集成

### 方法一：模块依赖（推荐）

1. 拷贝 `kline-marker-lib` 到项目根目录
2. 在 `settings.gradle.kts` 中添加：

```kotlin
include(":kline-marker-lib")
```

3. 在 `app/build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":kline-marker-lib"))
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
```

### 方法二：AAR文件

```kotlin
dependencies {
    implementation("com.alex:kline-marker:1.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
```

## 💡 最小示例

```java
// 1. 创建数据适配器
MyKLineDataAdapter adapter = new MyKLineDataAdapter();

// 2. 初始化标记管理器
KLineMarkerManager<MyKLineData> markerManager =
        new KLineMarkerManager.Builder<MyKLineData>()
                .context(this)
                .chart(combinedChart)
                .dataAdapter(adapter)
                .build();

// 3. 设置数据
markerManager.

setKLineData(klineDataList);

// 4. 添加标记
List<MarkerData> markers = new ArrayList<>();
markers.

add(new MarkerData(date, MarkerData.MarkerType.BUY, "买入"));
        markerManager.

setMarkers(markers);

// 5. 刷新显示
markerManager.

refresh();
```

## 🔧 高级配置

```java
// 标记配置
MarkerConfig markerConfig = new MarkerConfig.Builder()
                .markerSize(14f)
                .buyColor(Color.parseColor("#4CAF50"))
                .sellColor(Color.parseColor("#F44336"))
                .build();

// 趋势区间配置
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
        .risingColor(Color.parseColor("#4CAF50"))
        .fallingColor(Color.parseColor("#F44336"))
        .enableBezierCurve(true)
        .enableGradient(true)
        .build();

// 应用配置
KLineMarkerManager<MyKLineData> markerManager =
        new KLineMarkerManager.Builder<MyKLineData>()
                .context(this)
                .chart(chart)
                .dataAdapter(adapter)
                .markerConfig(markerConfig)
                .trendRegionConfig(trendConfig)
                .build();
```

## 🌟 核心优势

1. **专业级视觉效果**：精心设计的标记样式和趋势区间阴影
2. **高性能渲染**：智能的可视区域剪裁和缓存机制
3. **灵活扩展**：支持自定义绘制器和动画效果
4. **简单易用**：Builder模式和清晰的API设计
5. **完善文档**：从快速开始到深度定制的完整指南

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

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个库！

## 📧 联系

如有问题或建议，请通过 GitHub Issues 联系我们。

---

⭐ 如果这个库对您有帮助，请给我们一个 Star！ 