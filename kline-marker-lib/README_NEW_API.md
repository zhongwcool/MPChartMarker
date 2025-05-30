# K线标记库 - 新API使用指南

## 设计改进

新的设计移除了容易混淆的 `MarkerType` 和 `MarkerStyle`，改为使用更清晰的结构：

- `MarkerShape` - 定义标记的形状（矩形、圆形、三角形等）
- `MarkerPosition` - 定义标记相对于K线的位置（上方、下方、自动）
- `MarkerConfig` - 包含所有视觉属性的配置类
- `MarkerPresets` - 预定义的常用标记配置

## 基本使用

### 1. 使用预定义标记

```java
// 创建买入标记
MarkerData buyMarker = MarkerData.createBuyMarker(date, "买入");

// 创建卖出标记
MarkerData sellMarker = MarkerData.createSellMarker(date, "卖出");

// 创建警告标记
MarkerData warningMarker = MarkerData.createWarningMarker(date, "警告！");

// 创建信息标记
MarkerData infoMarker = MarkerData.createInfoMarker(date, "提示信息");

// 创建事件标记
MarkerData eventMarker = MarkerData.createEventMarker(date, "重要事件");

// 创建止损标记
MarkerData stopLossMarker = MarkerData.createStopLossMarker(date, "止损");

// 创建止盈标记
MarkerData takeProfitMarker = MarkerData.createTakeProfitMarker(date, "止盈");

// 创建纯文本标记
MarkerData textMarker = MarkerData.createTextMarker(date, "纯文本");

// 创建重要标记（五角星）
MarkerData importantMarker = MarkerData.createImportantMarker(date, "重要");

// 创建简单点标记（不显示文字）
MarkerData dotMarker = MarkerData.createDotMarker(date);
```

### 2. 自定义标记配置

```java
// 方式1：使用Builder模式创建自定义配置
MarkerConfig customConfig = new MarkerConfig.Builder()
    .shape(MarkerShape.CIRCLE)
    .position(MarkerPosition.ABOVE)
    .backgroundColor(0xFF00FF00)  // 绿色
    .textColor(0xFFFFFFFF)        // 白色
    .markerSize(30f)              // 30dp
    .textSize(14f)                // 14sp
    .showText(true)
    .showLine(true)
    .lineLength(LineLength.LONG)
    .dashedLine(true)
    .dashPattern(new float[]{10f, 5f})
    .alpha(0.8f)
    .zIndex(5)
    .build();

MarkerData customMarker = new MarkerData(date, "自定义", customConfig);

// 方式2：基于预设进行自定义
MarkerData customMarker2 = MarkerData.createCustomMarker(
    date, 
    "自定义", 
    MarkerPresets.customize(MarkerPresets.info())
        .backgroundColor(0xFF00FF00)
        .markerSize(35f)
        .build()
);
```

### 3. 使用自定义图标

```java
// 创建自定义图标标记
Drawable customIcon = ContextCompat.getDrawable(context, R.drawable.ic_custom);
MarkerConfig iconConfig = new MarkerConfig.Builder()
    .customIcon(customIcon)  // 自动设置shape为CUSTOM_ICON
    .position(MarkerPosition.ABOVE)
    .showText(true)
    .showLine(true)
    .build();

MarkerData iconMarker = new MarkerData(date, "自定义图标", iconConfig);
```

### 4. 批量创建标记

```java
List<MarkerData> markers = new ArrayList<>();

// 使用预设快速创建
markers.add(MarkerData.createBuyMarker(date1, "买入点"));
markers.add(MarkerData.createSellMarker(date2, "卖出点"));
markers.add(MarkerData.createWarningMarker(date3, "风险警告"));

// 批量自定义
MarkerConfig.Builder baseBuilder = new MarkerConfig.Builder()
    .position(MarkerPosition.AUTO)
    .showLine(true)
    .lineLength(LineLength.MEDIUM);

for (Date date : importantDates) {
    markers.add(new MarkerData(
        date,
        "事件",
        baseBuilder.shape(MarkerShape.STAR).build()
    ));
}
```

## 配置属性说明

### MarkerShape（标记形状）
- `RECTANGLE` - 矩形
- `CIRCLE` - 圆形
- `TRIANGLE_UP` - 向上三角形
- `TRIANGLE_DOWN` - 向下三角形
- `DIAMOND` - 菱形
- `STAR` - 五角星
- `DOT` - 圆点
- `CROSS` - 十字
- `ARROW_UP` - 向上箭头
- `ARROW_DOWN` - 向下箭头
- `CUSTOM_ICON` - 自定义图标
- `NONE` - 无形状（仅文字）

### MarkerPosition（标记位置）
- `ABOVE` - K线上方
- `BELOW` - K线下方
- `AUTO` - 自动选择（根据K线位置）

### LineLength（引线长度）
- `SHORT` - 短
- `MEDIUM` - 中等
- `LONG` - 长

### MarkerConfig属性
- `shape` - 形状
- `position` - 位置
- `showText` - 是否显示文字
- `showLine` - 是否显示引线
- `backgroundColor` - 背景颜色
- `textColor` - 文字颜色
- `lineColor` - 引线颜色
- `markerSize` - 标记大小（dp）
- `textSize` - 文字大小（sp）
- `lineWidth` - 引线宽度（dp）
- `lineLength` - 引线长度
- `isDashedLine` - 是否为虚线
- `dashPattern` - 虚线样式 [线段长度, 间隔长度]
- `customIcon` - 自定义图标
- `alpha` - 透明度 (0-1)
- `zIndex` - 绘制层级（数值越大越靠前）

## 预定义标记样式

| 预设方法 | 形状 | 颜色 | 位置 | 说明 |
|---------|------|------|------|------|
| `buy()` | 向上三角形 | 绿色 | 下方 | 买入信号 |
| `sell()` | 向下三角形 | 红色 | 上方 | 卖出信号 |
| `warning()` | 圆形 | 橙色 | 上方 | 警告提示 |
| `info()` | 圆形 | 蓝色 | 自动 | 信息提示 |
| `success()` | 圆形 | 绿色 | 上方 | 成功标记 |
| `error()` | 圆形 | 红色 | 上方 | 错误标记 |
| `event()` | 菱形 | 紫色 | 自动 | 事件标记 |
| `stopLoss()` | 向下箭头 | 深红色 | 上方 | 止损标记 |
| `takeProfit()` | 向上箭头 | 深绿色 | 下方 | 止盈标记 |
| `textOnly()` | 无形状 | 黑色文字 | 自动 | 纯文本 |
| `important()` | 五角星 | 黄色 | 上方 | 重要标记 |
| `dot()` | 圆点 | 灰色 | 自动 | 简单点 |
| `cross()` | 十字 | 黑色 | 自动 | 定位标记 |

## 迁移指南

从旧API迁移到新API：

```java
// 旧API
MarkerData marker = new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, "买入", color);

// 新API - 使用预设
MarkerData marker = MarkerData.createBuyMarker(date, "买入");

// 新API - 自定义
MarkerData marker = new MarkerData(date, "买入", 
    new MarkerConfig.Builder()
        .shape(MarkerShape.RECTANGLE)
        .backgroundColor(color)
        .showText(true)
        .build()
);
``` 