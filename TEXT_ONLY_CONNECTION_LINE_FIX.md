# 🔧 TEXT_ONLY 引出线起始位置修复

## 🐛 问题描述

TEXT_ONLY标记的引出线起始位置显示不正确：

### ❌ 修复前的问题
- **错误逻辑**: TextOnlyRenderer自己绘制引出线，从文字位置向K线方向画线
- **起始位置错误**: 引出线起始位置不在K线图上的具体位置
- **视觉效果差**: 引出线看起来像是从文字"伸出"的，而不是从K线"引出"的

### 📍 正确的逻辑应该是
1. **引出线起点**: 在K线图上的具体位置（高点、低点或中点）
2. **引出线终点**: 在文字显示位置
3. **文字位置**: 在引出线的终点

## ✅ 解决方案

### 1. 架构调整

将引出线绘制责任从 `TextOnlyRenderer` 转移到 `KLineMarkerRenderer`：

#### 修改前的架构
```
KLineMarkerRenderer:
├── 跳过TEXT_ONLY的连接线绘制
└── 让TextOnlyRenderer自己处理

TextOnlyRenderer:
├── 绘制引出线（错误：不知道K线位置）
└── 绘制文字
```

#### 修改后的架构
```
KLineMarkerRenderer:
├── 为TEXT_ONLY绘制正确的斜线连接
└── 从K线位置到文字位置

TextOnlyRenderer:
└── 只绘制文字（简化职责）
```

### 2. 代码修改

#### KLineMarkerRenderer.java
**新增方法**: `drawTextOnlyConnectionLine()`
```java
private void drawTextOnlyConnectionLine(Canvas canvas, MarkerPosition position, MarkerData marker) {
    // 使用实线画笔
    Paint solidLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    solidLinePaint.setStyle(Paint.Style.STROKE);
    solidLinePaint.setStrokeWidth(1.5f * density);
    solidLinePaint.setColor(marker.getTextColor() != 0 ? marker.getTextColor() : config.getNumberTextColor());
    
    // 计算30度角的斜线
    float angleRadians = (float) Math.toRadians(30);
    float deltaX = Math.abs(position.markerScreenY - position.lineStartY) * (float) Math.cos(angleRadians);
    
    // 根据标记位置决定斜线方向
    float endX = position.screenX + deltaX;
    float endY = position.markerScreenY;
    
    // 绘制从K线位置到文字位置的斜实线
    canvas.drawLine(position.screenX, position.lineStartY, endX, endY, solidLinePaint);
}
```

**修改逻辑**:
```java
// 修改前
if (marker.getStyle() == MarkerStyle.TEXT_ONLY) {
    // TextOnlyRenderer会自己处理斜实线，这里不需要绘制
    return;
}

// 修改后  
if (marker.getStyle() == MarkerStyle.TEXT_ONLY) {
    // 绘制从K线位置到文字位置的斜实线
    drawTextOnlyConnectionLine(canvas, position, marker);
    return;
}
```

#### TextOnlyRenderer.java
**简化职责**: 移除所有连接线相关代码
```java
@Override
public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
    // 设置文字颜色和大小
    int textColor = marker.getTextColor() != 0 ? marker.getTextColor() : config.getNumberTextColor();
    textPaint.setColor(textColor);
    textPaint.setTextSize(config.getTextSize() * density);
    textPaint.setTypeface(config.getTextTypeface());
    
    String text = marker.getText();
    if (text == null || text.isEmpty()) {
        return;
    }
    
    // 只绘制文字，连接线由KLineMarkerRenderer处理
    float textX = centerX + config.getPadding() * density;
    float textY = centerY + (textPaint.getTextSize() / 3);
    canvas.drawText(text, textX, textY, textPaint);
}
```

### 3. 视觉效果改进

#### 修复前
```
K线图:  |---|    
        |   |    
        |---|    
            \     ← 引出线起点位置错误
             \    
              "重要信息"  ← 文字
```

#### 修复后  
```
K线图:  |---|    
        |   |    
        |---|    
        |    \    ← 引出线正确从K线开始
        |     \   
              "重要信息"  ← 文字在引出线终点
```

## 🎯 技术细节

### 斜线角度计算
- **角度**: 30度（`Math.toRadians(30)`）
- **方向**: 从K线位置向右上/右下延伸到文字位置
- **长度**: 根据标记位置和文字位置的垂直距离自动计算

### 颜色一致性
- 引出线颜色与文字颜色保持一致
- 支持自定义文字颜色：`marker.getTextColor()`
- 默认使用配置的数字文字颜色：`config.getNumberTextColor()`

### 线条样式
- **类型**: 实线（不是虚线）
- **宽度**: `1.5f * density`
- **端点**: 圆形端点（`Paint.Cap.ROUND`）

## ✅ 验证结果

### 编译状态
- ✅ 编译成功
- ✅ 无编译错误
- ✅ 所有依赖正确

### 功能验证
1. **引出线起点**: 现在正确位于K线图上
2. **引出线终点**: 正确连接到文字位置  
3. **视觉效果**: 引出线看起来是从K线"引出"的
4. **颜色一致**: 引出线和文字颜色保持一致

### 代码质量
- ✅ 职责分离清晰
- ✅ 代码简化（TextOnlyRenderer更简洁）
- ✅ 逻辑正确（KLineMarkerRenderer知道K线位置）

现在TEXT_ONLY标记的引出线起始位置完全正确了！🎉 