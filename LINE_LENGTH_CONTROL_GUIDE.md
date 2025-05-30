# K线标记虚线长度控制指南

## 概述

K线标记系统支持灵活的虚线长度控制，用于控制标记与K线数据点之间的距离。所有标记都保持与K线的连接，只是距离远近不同。

## 虚线长度类型

系统提供了5种距离类型：

### LineLength 枚举值

| 类型 | 说明 | 距离 | 使用场景 |
|------|------|------|----------|
| `NONE` | 极短距离 | 3dp | 标记紧贴K线数据点，避免重叠 |
| `SHORT` | 短距离 | 8dp | 密集显示区域 |
| `MEDIUM` | 中等距离 | 20dp | 默认距离，平衡美观与功能 |
| `LONG` | 长距离 | 35dp | 重要标记，突出显示 |
| `EXTRA_LONG` | 超长距离 | 55dp | 特别重要标记或避免重叠 |

**重要说明**：`NONE` 类型并不表示无连接线，而是表示极短的连接距离，标记仍然与K线数据点保持连接。

## 基本用法

### 1. 创建标记时指定距离

```java
// 使用便捷方法创建带指定距离的标记
MarkerData buyMarker = MarkerData.createBuyMarker(
    date, 
    "买入", 
    LineLength.SHORT  // 短距离，密集显示
);

MarkerData sellMarker = MarkerData.createSellMarker(
    date, 
    "卖出", 
    LineLength.MEDIUM  // 中等距离，默认
);

MarkerData textMarker = MarkerData.createTextMarker(
    date, 
    "重要信息", 
    LineLength.LONG  // 长距离，突出显示
);
```

### 2. 修改现有标记的距离

```java
MarkerData marker = new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, "买入");
marker.setLineLength(LineLength.NONE);  // 设置为极短距离，紧贴K线
```

### 3. 极短距离标记示例

```java
// 创建极短距离的标记，适合避免重叠
MarkerData compactMarker = new MarkerData(
    date,
    MarkerType.BUY,
    MarkerStyle.RECTANGLE_TEXT,
    "买"
);
compactMarker.setLineLength(LineLength.NONE);  // 极短距离，紧贴但仍连接
```

## 不同标记样式的虚线行为

### 1. 文字标记（TEXT_ONLY）
- 文字标记使用**斜实线+文字**的形式（30度角）
- 由 `TextOnlyRenderer` 内部绘制斜实线和文字
- 支持所有虚线长度类型
- 与其他标记的垂直虚线形成区别

```java
MarkerData textMarker = MarkerData.createTextMarker(
    date, 
    "财报发布", 
    LineLength.EXTRA_LONG  // 使用超长斜实线突出显示
);
```

### 2. 三角形标记（TRIANGLE_UP/DOWN）
- 通常用于数据激增/骤降
- 建议使用 `NONE` 或 `SHORT`，避免视觉干扰
- 使用垂直虚线连接

```java
MarkerData surgeMarker = MarkerData.createSurgeMarker(date);
surgeMarker.setLineLength(LineLength.NONE);  // 无虚线，更简洁
```

### 3. 形状+文字标记（RECTANGLE_TEXT/CIRCLE_TEXT/DIAMOND_TEXT）
- 支持所有虚线长度
- 使用垂直虚线连接
- 根据重要性选择合适长度

```java
// 重要的止损标记使用短虚线
MarkerData stopLoss = new MarkerData(
    date, MarkerType.STOP_LOSS, MarkerStyle.DIAMOND_TEXT, "止损"
);
stopLoss.setLineLength(LineLength.SHORT);

// 重要事件使用长虚线
MarkerData event = new MarkerData(
    date, MarkerType.EVENT, MarkerStyle.CIRCLE_TEXT, "财报"
);
event.setLineLength(LineLength.LONG);
```

## 使用建议

### 1. 避免重叠的策略
- 相邻标记使用不同距离长度
- 密集区域使用 `NONE` 或 `SHORT`（紧贴显示）
- 重要标记使用 `LONG` 或 `EXTRA_LONG`（突出显示）

### 2. 视觉层次
- **EXTRA_LONG**: 最重要的标记（如重大事件），拉远距离突出
- **LONG**: 重要标记（如事件、警告），适度拉开距离
- **MEDIUM**: 常规标记（默认选择），平衡的距离
- **SHORT**: 次要标记（如小额交易），较近距离
- **NONE**: 密集显示标记，极短距离但保持连接

### 3. 性能考虑
- `NONE` 类型的标记渲染最快（距离最短）
- 所有类型都保持连接，不影响数据关联性

## 完整示例

```java
private void addMarkersWithDifferentLineLength() {
    List<MarkerData> markers = new ArrayList<>();
    
    // 重要买入信号 - 长距离突出显示
    MarkerData importantBuy = MarkerData.createBuyMarker(
        date1, "重要买入", LineLength.LONG
    );
    markers.add(importantBuy);
    
    // 普通卖出 - 中等距离  
    MarkerData normalSell = MarkerData.createSellMarker(
        date2, "卖出", LineLength.MEDIUM
    );
    markers.add(normalSell);
    
    // 小额交易 - 短距离
    MarkerData smallTrade = MarkerData.createBuyMarker(
        date3, "小买", LineLength.SHORT
    );
    markers.add(smallTrade);
    
    // 密集显示标记 - 极短距离
    MarkerData compactMarker = new MarkerData(
        date4, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, "买"
    );
    compactMarker.setLineLength(LineLength.NONE);
    markers.add(compactMarker);
    
    // 重要事件 - 超长距离，最突出
    MarkerData majorEvent = MarkerData.createEventMarker(date5, "重大消息");
    majorEvent.setLineLength(LineLength.EXTRA_LONG);
    markers.add(majorEvent);
    
    // 数据激增 - 极短距离，简洁显示
    MarkerData surge = MarkerData.createSurgeMarker(date6);
    surge.setLineLength(LineLength.NONE);
    markers.add(surge);
    
    markerManager.setMarkers(markers);
}
```

## 技术实现细节

### LineLengthUtils 工具类
- 提供虚线长度到像素的转换
- 支持不同屏幕密度的适配
- 提供便捷的判断方法

### 渲染器更新
- 所有渲染器都已更新支持虚线长度控制
- `TextOnlyRenderer` 特别优化了引出线处理
- 保持向后兼容性

### 性能优化
- 虚线长度计算只在绘制时进行
- 使用缓存减少重复计算
- `NONE` 类型跳过所有连接线绘制

通过灵活的虚线长度控制，您可以创建更加美观和实用的K线标记系统，有效避免重叠问题并突出重要信息！ 