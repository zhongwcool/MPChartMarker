# K线标记库演示应用

这是一个完整的Android演示应用，展示了如何使用 `kline-marker-lib` 库来实现K线图的标记功能和趋势背景效果。

## 📁 项目结构

```
sample/
├── src/main/java/com/alex/mpchart/sample/
│   ├── MainActivity.java          # 主活动
│   ├── FirstFragment.java         # K线图演示页面
│   ├── SecondFragment.java        # 使用指南页面
│   ├── KLineData.java             # K线数据模型
│   └── KLineDataAdapterImpl.java  # 数据适配器实现
├── src/main/res/
│   └── layout/
│       ├── activity_main.xml      # 主活动布局
│       ├── fragment_first.xml     # 演示页面布局
│       └── fragment_second.xml    # 指南页面布局
├── build.gradle.kts               # 构建配置
└── README.md                      # 本文件
```

## 🚀 功能特性

### 1. K线图基础功能

- 标准蜡烛图（K线图）显示
- 触摸缩放和拖动支持
- 自定义图表样式配置

### 2. 标记功能演示

- **买入标记** 🟢 - 绿色圆角矩形
- **卖出标记** 🔴 - 红色圆角矩形
- **数字标记** 🔵 - 蓝色圆形，支持数字/文字
- **上三角标记** 🔶 - 橙色向上箭头
- **下三角标记** 🔶 - 橙色向下箭头

### 3. 趋势区间背景

- **上涨趋势** - 绿色渐变背景
- **下跌趋势** - 红色渐变背景
- **中性趋势** - 蓝色渐变背景

## 🎮 操作指南

### 启动应用

1. 编译并安装应用到设备
2. 应用自动加载30天模拟K线数据
3. 可以立即开始体验各种功能

### 演示操作

- **添加标记**：点击按钮添加各类型标记
- **添加趋势**：点击按钮添加趋势区间背景
- **清除标记**：清除所有标记
- **清除全部**：清除标记和趋势区间
- **查看指南**：切换到使用指南页面

### 交互功能

- 双指缩放图表
- 单指拖动平移
- 查看具体数据点

## 🔧 技术实现

### 依赖配置

```kotlin
dependencies {
    implementation(project(":kline-marker-lib"))
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // 其他标准Android依赖...
}
```

### 核心代码示例

#### 数据适配器

```java
public class KLineDataAdapterImpl implements KLineDataAdapter<KLineData> {
    @Override
    public Date getDate(KLineData klineData) {
        return klineData.getDate();
    }
    // 实现其他接口方法...
}
```

#### 标记管理器初始化

```java
KLineMarkerManager<KLineData> markerManager =
        new KLineMarkerManager.Builder<KLineData>()
                .context(requireContext())
                .chart(chart)
                .dataAdapter(adapter)
                .markerConfig(markerConfig)
                .trendRegionConfig(trendConfig)
                .build();
```

#### 添加标记

```java
List<MarkerData> markers = new ArrayList<>();
markers.add(new MarkerData(dateFormat.parse("2024-01-05"), MarkerData.MarkerType.BUY, "买入"));
markerManager.setMarkers(markers);
markerManager.refresh();
```

## 📊 演示数据

### K线数据

- **时间范围**：2024年1月1日-30日
- **数据类型**：随机生成的模拟股价
- **价格基准**：100点，±10%波动范围

### 标记数据

- `2024-01-05` - 买入标记
- `2024-01-10` - 数字标记 "1"
- `2024-01-15` - 卖出标记
- `2024-01-20` - 上三角标记
- `2024-01-25` - 下三角标记

### 趋势区间

- `2024-01-03~12` - 上涨趋势（绿色）
- `2024-01-13~17` - 中性趋势（蓝色）
- `2024-01-18~28` - 下跌趋势（红色）

## 🎯 学习要点

通过这个演示应用，您可以学到：

1. **集成步骤**：如何将kline-marker-lib集成到项目中
2. **数据适配**：如何适配自己的K线数据格式
3. **配置使用**：如何自定义标记和趋势区间的外观
4. **API调用**：如何使用库提供的各种方法
5. **性能优化**：如何在大量数据场景下保持流畅性能

## 🔄 扩展方向

### 数据源扩展

- 集成真实股票API（如Yahoo Finance、Alpha Vantage）
- 支持加密货币数据（如Binance、CoinGecko）
- 添加外汇数据支持

### 功能扩展

- 自定义标记类型和样式
- 添加标记点击事件处理
- 实现标记数据持久化存储
- 支持标记的拖拽移动

### 界面扩展

- 添加更多图表类型（分时图、深度图等）
- 实现多时间周期切换
- 添加技术指标叠加显示

## 🚗 快速开始

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd MPChartMarker
   ```

2. **构建项目**
   ```bash
   ./gradlew :sample:assembleDebug
   ```

3. **安装到设备**
   ```bash
   ./gradlew :sample:installDebug
   ```

4. **开始体验**
   启动应用即可看到完整的K线标记功能演示

## 📝 注意事项

- 确保设备API级别 ≥ 31（Android 12+）
- 建议在真机上测试触摸交互功能
- 首次运行会生成随机数据，每次重启数据会重新生成
- 支持横竖屏旋转，图表会自动适配

## 🤝 技术支持

如果在使用过程中遇到问题：

1. 查看完整的使用指南（应用内第二个页面）
2. 检查日志输出排查具体错误
3. 参考库文档了解更多高级用法
4. 提交Issue获取技术支持

---

这个演示应用展示了 `kline-marker-lib` 的核心功能，帮助开发者快速理解和集成这个强大的K线标记库。 