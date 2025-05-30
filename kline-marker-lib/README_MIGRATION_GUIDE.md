# K线标记库 - API迁移指南

## 概述

新版本重新设计了标记系统，移除了容易混淆的 `MarkerType` 和 `MarkerStyle`，改为使用更清晰的配置系统。

## 主要变化

### 1. 删除的类
- `MarkerType` - 标记类型枚举
- `MarkerStyle` - 标记样式枚举

### 2. 新增的类
- `MarkerShape` - 标记形状枚举
- `MarkerPosition` - 标记位置枚举
- `MarkerConfig` (data包) - 标记配置类
- `MarkerPresets` - 预定义标记配置

### 3. 更新的类
- `MarkerData` - 简化构造函数，使用MarkerConfig
- `IMarkerRenderer` - 方法签名更新
- 所有Renderer类 - 适配新的API

## 迁移步骤

### 步骤1：更新MarkerData创建

**旧方式：**
```java
// 复杂的构造函数
MarkerData marker = new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, "买入", color);

// 设置属性
marker.setLineLength(LineLength.SHORT);
marker.setSize(20f);
```

**新方式：**
```java
// 使用预设
MarkerData marker = MarkerData.createBuyMarker(date, "买入");

// 或自定义配置
MarkerData marker = new MarkerData(date, "买入", 
    new MarkerConfig.Builder()
        .shape(MarkerShape.RECTANGLE)
        .backgroundColor(0xFF4CAF50)
        .position(MarkerPosition.BELOW)
        .build()
);
```

### 步骤2：更新预定义标记

**旧方式：**
```java
MarkerData.createBuyMarker(date, "买入");
MarkerData.createSellMarker(date, "卖出");
MarkerData.createEventMarker(date, "事件");
```

**新方式：**
```java
MarkerData.createBuyMarker(date, "买入");      // 保持不变
MarkerData.createSellMarker(date, "卖出");     // 保持不变
MarkerData.createEventMarker(date, "事件");    // 保持不变
MarkerData.createWarningMarker(date, "警告");  // 新增
MarkerData.createInfoMarker(date, "信息");     // 新增
```

### 步骤3：更新自定义渲染器

**旧IMarkerRenderer：**
```java
public interface IMarkerRenderer {
    void drawMarker(Canvas canvas, float centerX, float centerY, 
                   MarkerData marker, MarkerConfig config, Context context);
    float getMarkerWidth(MarkerData marker, MarkerConfig config);
    float getMarkerHeight(MarkerData marker, MarkerConfig config);
    boolean supportsStyle(MarkerStyle style);
}
```

**新IMarkerRenderer：**
```java
public interface IMarkerRenderer {
    void drawMarker(Canvas canvas, float centerX, float centerY, 
                   MarkerData marker, Context context);
    float getMarkerWidth(MarkerData marker);
    float getMarkerHeight(MarkerData marker);
    boolean supportsShape(MarkerShape shape);
}
```

### 步骤4：更新自定义渲染器实现

**旧实现：**
```java
public class MyRenderer implements IMarkerRenderer {
    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, 
                          MarkerData marker, MarkerConfig config, Context context) {
        int color = marker.getColor() != 0 ? marker.getColor() : config.getBuyColor();
        float size = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        // ...
    }
    
    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.CUSTOM;
    }
}
```

**新实现：**
```java
public class MyRenderer implements IMarkerRenderer {
    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, 
                          MarkerData marker, Context context) {
        int color = marker.getConfig().getBackgroundColor();
        float size = marker.getConfig().getMarkerSize();
        // ...
    }
    
    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.CUSTOM_ICON;
    }
}
```

### 步骤5：更新KLineMarkerRenderer使用

**旧方式：**
```java
KLineMarkerRenderer renderer = new KLineMarkerRenderer(context, chart, adapter, config);
renderer.registerRenderer(MarkerStyle.CUSTOM, myRenderer);
```

**新方式：**
```java
KLineMarkerRenderer renderer = new KLineMarkerRenderer(context, chart, adapter);
renderer.registerRenderer(MarkerShape.CUSTOM_ICON, myRenderer);
```

## 新功能

### 1. 预定义配置
```java
// 快速使用预定义样式
MarkerData warning = MarkerData.createWarningMarker(date, "风险提醒");
MarkerData info = MarkerData.createInfoMarker(date, "重要信息");
MarkerData stopLoss = MarkerData.createStopLossMarker(date, "止损");
```

### 2. 灵活的配置系统
```java
// 使用Builder模式自定义
MarkerConfig config = new MarkerConfig.Builder()
    .shape(MarkerShape.CIRCLE)
    .backgroundColor(0xFF00FF00)
    .textColor(0xFFFFFFFF)
    .markerSize(30f)
    .position(MarkerPosition.ABOVE)
    .showLine(true)
    .dashedLine(false)
    .alpha(0.8f)
    .build();
```

### 3. 基于预设的自定义
```java
// 基于现有预设进行微调
MarkerData custom = MarkerData.createCustomMarker(date, "自定义",
    MarkerPresets.customize(MarkerPresets.info())
        .backgroundColor(0xFF00FF00)
        .markerSize(35f)
        .build()
);
```

## 常见问题

### Q: 如何处理原来的MarkerType.NONE？
A: 不再需要，直接不创建MarkerData即可。

### Q: 如何实现原来的MarkerStyle.TEXT_ONLY？
A: 使用 `MarkerShape.NONE` 或 `MarkerPresets.textOnly()`

### Q: 颜色配置在哪里？
A: 在MarkerConfig中，通过backgroundColor、textColor、lineColor设置

### Q: 如何设置标记位置？
A: 通过MarkerPosition枚举：ABOVE、BELOW、AUTO

### Q: 虚线样式如何配置？
A: 通过MarkerConfig的isDashedLine和dashPattern属性

## 完整示例

```java
// 创建标记列表
List<MarkerData> markers = new ArrayList<>();

// 使用预设
markers.add(MarkerData.createBuyMarker(date1, "买入"));
markers.add(MarkerData.createSellMarker(date2, "卖出"));
markers.add(MarkerData.createWarningMarker(date3, "风险"));

// 自定义标记
markers.add(new MarkerData(date4, "自定义",
    new MarkerConfig.Builder()
        .shape(MarkerShape.STAR)
        .backgroundColor(0xFFFFD700)
        .position(MarkerPosition.ABOVE)
        .showText(true)
        .markerSize(28f)
        .build()
));

// 基于预设自定义
markers.add(MarkerData.createCustomMarker(date5, "特殊",
    MarkerPresets.customize(MarkerPresets.important())
        .backgroundColor(0xFFFF0000)
        .build()
));

// 设置到渲染器
renderer.setMarkers(markers);
``` 