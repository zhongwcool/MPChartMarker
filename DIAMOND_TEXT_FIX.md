# 菱形标记文字显示修复

## 🔧 问题描述

菱形标记中的文字没有正常显示，特别是"止损"等文字在菱形内看不见。

## 🔍 问题原因分析

1. **文字颜色问题**：文字颜色可能与背景色相同或对比度不够
2. **文字大小问题**：文字可能过小，特别是长文字如"止损"
3. **文字位置问题**：文字垂直居中计算可能不准确

## ✅ 修复方案

### 1. 自动颜色对比
```java
// 根据背景色自动选择对比色
int textColor = marker.getTextColor();
if (textColor == 0) {
    if (isLightColor(bgColor)) {
        textColor = 0xFF000000; // 背景浅色用黑字
    } else {
        textColor = 0xFFFFFFFF; // 背景深色用白字
    }
}
```

### 2. 智能文字大小调整
```java
// 根据文字长度调整大小
float baseTextSize = config.getTextSize() * density;
if (text.length() <= 1) {
    textSize = baseTextSize * 0.7f; // 单字符
} else if (text.length() <= 2) {
    textSize = baseTextSize * 0.6f; // 两字符如"止损"
} else {
    textSize = baseTextSize * 0.45f; // 多字符
}

// 确保文字不会太小
textSize = Math.max(textSize, 8f * density);
```

### 3. 精确文字定位
```java
// 使用FontMetrics精确计算垂直居中位置
Paint.FontMetrics fm = textPaint.getFontMetrics();
float textHeight = fm.descent - fm.ascent;
float textY = centerY + textHeight / 2 - fm.descent;
```

### 4. 菱形大小自适应
```java
// 长文字时菱形自动放大
float sizeMultiplier = 1.0f;
if (text != null && text.length() > 2) {
    sizeMultiplier = 1.2f; // 长文字时菱形稍大
}
```

## 🎯 示例代码优化

在示例中显式设置文字颜色，确保可见性：

```java
// 止损标记 - 粉红色背景配白色文字
MarkerData stopLossMarker = new MarkerData(
    date, MarkerType.STOP_LOSS, MarkerStyle.DIAMOND_TEXT, "止损"
);
stopLossMarker.setTextColor(Color.WHITE); // 确保在粉红色背景上可见

// 警告标记 - 橙色背景配白色文字  
MarkerData warningMarker = new MarkerData(
    date, MarkerType.WARNING, MarkerStyle.CIRCLE_TEXT, "!"
);
warningMarker.setTextColor(Color.WHITE); // 确保在橙色背景上可见
```

## 🔧 技术实现细节

### 颜色对比度计算
```java
private boolean isLightColor(int color) {
    int red = (color >> 16) & 0xFF;
    int green = (color >> 8) & 0xFF;
    int blue = color & 0xFF;
    
    // 计算亮度 (0.299*R + 0.587*G + 0.114*B)
    double brightness = (red * 0.299 + green * 0.587 + blue * 0.114);
    return brightness > 127;
}
```

### 文字大小适配策略
- **单字符**：0.7倍基础大小
- **两字符**（如"止损"）：0.6倍基础大小  
- **多字符**：0.45倍基础大小
- **最小尺寸**：不小于8dp，确保可读性

## ✅ 修复效果

修复后的菱形标记具有以下特性：

1. **自动颜色对比**：文字颜色自动与背景形成足够对比度
2. **大小自适应**：文字大小根据长度智能调整
3. **精确定位**：文字在菱形中精确垂直居中
4. **向后兼容**：不影响现有代码，只是增强显示效果

现在菱形标记中的"止损"等文字应该能够清晰可见！ 