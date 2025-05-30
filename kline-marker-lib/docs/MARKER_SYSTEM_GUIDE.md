# K线标记系统使用指南

## 概述

新的K线标记系统采用了更加灵活和可扩展的设计，将标记的业务含义（MarkerType）和视觉样式（MarkerStyle）分离，使得开发者可以自由组合不同的类型和样式，创建丰富多样的标记效果。

## 核心概念

### 1. MarkerType（标记类型）
定义标记的业务含义，包括：
- `NONE` - 无标记
- `BUY` - 买入信号
- `SELL` - 卖出信号
- `STOP_LOSS` - 止损
- `TAKE_PROFIT` - 止盈
- `EVENT` - 重要事件
- `SURGE` - 数据激增
- `PLUNGE` - 数据骤降
- `WARNING` - 警告
- `INFO` - 信息提示
- `CUSTOM` - 自定义类型

### 2. MarkerStyle（标记样式）
定义标记的视觉表现，包括：
- `RECTANGLE_TEXT` - 矩形背景 + 文字
- `CIRCLE_TEXT` - 圆形背景 + 文字
- `TRIANGLE_UP` - 上三角形
- `TRIANGLE_DOWN` - 下三角形
- `TEXT_ONLY` - 纯文字（带引出线）
- `DIAMOND_TEXT` - 菱形背景 + 文字
- `STAR` - 五角星（待实现）
- `DOT` - 圆点（待实现）
- `CROSS` - 十字标记（待实现）
- `ARROW_UP` - 向上箭头（待实现）
- `ARROW_DOWN` - 向下箭头（待实现）
- `CUSTOM_ICON` - 自定义图标（待实现）

## 使用示例

### 基本使用

```java
// 1. 创建买入标记
MarkerData buyMarker = new MarkerData(
    new Date(2024, 1, 15),
    MarkerType.BUY,
    MarkerStyle.RECTANGLE_TEXT,
    "买入"
);

// 2. 创建卖出标记（自定义颜色）
MarkerData sellMarker = new MarkerData(
    new Date(2024, 1, 20),
    MarkerType.SELL,
    MarkerStyle.RECTANGLE_TEXT,
    "卖出",
    Color.RED
);

// 3. 使用便捷方法创建标记
MarkerData surgeMarker = MarkerData.createSurgeMarker(new Date(2024, 1, 25));
MarkerData eventMarker = MarkerData.createEventMarker(new Date(2024, 2, 5), "财报");
```

### 高级自定义

```java
// 创建完全自定义的标记
MarkerData customMarker = new MarkerData();
customMarker.setDate(new Date(2024, 3, 1));
customMarker.setType(MarkerType.CUSTOM);
customMarker.setStyle(MarkerStyle.CIRCLE_TEXT);
customMarker.setText("自定义");
customMarker.setColor(Color.parseColor("#9C27B0")); // 紫色背景
customMarker.setTextColor(Color.WHITE);              // 白色文字
customMarker.setSize(16);                            // 自定义大小
```

### 完整集成示例

```java
// 1. 创建配置
MarkerConfig config = new MarkerConfig.Builder()
    .markerSize(14f)
    .textSize(10f)
    .buyColor(Color.parseColor("#4CAF50"))
    .sellColor(Color.parseColor("#F44336"))
    .build();

// 2. 创建管理器
KLineMarkerManager manager = new KLineMarkerManager.Builder()
    .context(context)
    .chart(chart)
    .dataAdapter(dataAdapter)
    .markerConfig(config)
    .build();

// 3. 设置数据
manager.setKLineData(klineData);

// 4. 创建标记列表
List<MarkerData> markers = new ArrayList<>();
markers.add(MarkerData.createBuyMarker(date1, "买入"));
markers.add(MarkerData.createSellMarker(date2, "卖出"));
markers.add(MarkerData.createEventMarker(date3, "重要事件"));

// 5. 设置标记并刷新
manager.setMarkers(markers);
manager.refresh();
```

## 标记位置规则

不同类型的标记会自动定位到合适的位置：

1. **买入/止损标记** - 显示在K线低点下方
2. **卖出/止盈标记** - 显示在K线高点上方
3. **数据激增标记** - 显示在K线高点上方（使用上三角）
4. **数据骤降标记** - 显示在K线低点下方（使用下三角）
5. **其他标记** - 根据索引奇偶性交替显示在K线中点上下方

## 扩展自定义渲染器

如果内置的样式不满足需求，可以创建自定义渲染器：

```java
// 1. 实现IMarkerRenderer接口
public class CustomRenderer implements IMarkerRenderer {
    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, 
                          MarkerData marker, MarkerConfig config) {
        // 自定义绘制逻辑
    }
    
    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        // 返回标记宽度
    }
    
    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        // 返回标记高度
    }
    
    @Override
    public boolean supportsStyle(MarkerStyle style) {
        // 返回是否支持指定样式
    }
}

// 2. 注册自定义渲染器
manager.getMarkerRenderer().registerRenderer(MarkerStyle.CUSTOM_ICON, new CustomRenderer());
```

## 最佳实践

1. **合理使用标记类型和样式的组合**
   - 买入/卖出使用矩形背景，醒目易识别
   - 数据变化使用三角形，直观表示方向
   - 事件信息使用圆形背景，柔和不突兀
   - 警告信息使用特殊颜色，引起注意

2. **控制标记数量**
   - 避免在同一区域显示过多标记
   - 使用不同的样式和颜色区分重要程度

3. **性能优化**
   - 只在可见区域绘制标记
   - 使用日期映射提高查找效率
   - 复用Paint对象减少内存分配

4. **自定义扩展**
   - 通过MarkerType.CUSTOM和自定义渲染器实现特殊需求
   - 保持渲染器的轻量级，避免复杂计算

## 迁移指南

从旧版本迁移到新标记系统：

```java
// 旧版本
MarkerData marker = new MarkerData(date, MarkerData.MarkerType.BUY, "买入");

// 新版本
MarkerData marker = new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, "买入");
// 或使用便捷方法
MarkerData marker = MarkerData.createBuyMarker(date, "买入");
```

主要变化：
1. MarkerType从内部枚举变为独立类
2. 新增MarkerStyle参数控制视觉样式
3. 支持更多自定义选项（文字颜色、大小等）
4. 提供便捷的工厂方法 