# 📏 动态虚线长度调整功能

## 🎯 功能概述

当标记显示位置可能超过图表区域时，系统会自动将标记约束在图表的可视区域内（最顶部或最底部），并动态调整虚线的长度以保持视觉协调。

## 🔧 实现原理

### 1. 边界检测与标记约束

```java
// 检查是否超出底部边界
if (markerScreenY > safeBottomY) {
    markerScreenY = safeBottomY;  // 约束到底部边界
    // 动态调整虚线长度
    actualLineLength = Math.max(0, markerScreenY - lineStartY);
}

// 检查是否超出顶部边界  
if (markerScreenY < safeTopY) {
    markerScreenY = safeTopY;     // 约束到顶部边界
    // 动态调整虚线长度
    actualLineLength = Math.max(0, lineStartY - markerScreenY);
}
```

### 2. 视觉效果优化

#### 正常虚线
- **外观**：标准虚线样式
- **长度**：配置的原始长度
- **透明度**：100%

#### 压缩虚线
- **外观**：更粗的线条（1.5倍宽度）
- **长度**：动态调整后的实际长度
- **透明度**：80%（稍微降低以区分状态）

### 3. 特殊情况处理

#### 零长度虚线
```java
if (position.actualLineLength <= 0) {
    // 不绘制连接线，标记直接贴在K线上
    return;
}
```

#### TEXT_ONLY标记的斜线调整
```java
// 基于实际距离计算斜线长度
float actualDistance = Math.abs(position.markerScreenY - position.lineStartY);
float deltaX = actualDistance * Math.cos(angleRadians);
```

## 📊 使用场景

### 场景1：图表缩放导致标记超出边界
```
原始情况：
K线 -------- 标记 (超出顶部)
  |
  |虚线(长)
  |

调整后：
标记 -------- (约束在顶部边界)
  |
  |虚线(短)
  |
K线 --------
```

### 场景2：多个标记重叠时的空间优化
```
原始情况：
标记A -------- (超出底部)
标记B -------- (超出底部)
  |
  |虚线们重叠
  |
K线 --------

调整后：
K线 --------
  |
  |虚线(短)
  |
标记A -------- (约束在底部边界)
标记B -------- (约束在底部边界)
```

## 🎨 视觉反馈系统

### 压缩状态指示器
当虚线被压缩时，系统提供以下视觉反馈：

1. **线条加粗**：从标准宽度增加到1.5倍
2. **透明度调整**：从100%降低到80%
3. **调试日志**：在DEBUG模式下输出压缩信息

### 颜色保持
- 虚线颜色始终与标记颜色保持一致
- 压缩状态不改变颜色，只改变粗细和透明度

## 🔍 调试信息

在DEBUG模式下，系统会输出详细的虚线调整信息：

```
虚线被压缩: 原始长度=30.0, 实际长度=15.0
TEXT_ONLY虚线被压缩: 原始长度=20.0, 实际长度=8.0
```

## ⚙️ 配置参数

### 边界贴合计算（2024优化版）
```java
// 获取图表边界
float contentTop = chart.getViewPortHandler().contentTop();
float contentBottom = chart.getViewPortHandler().contentBottom();

// 标记真正贴边显示 - 标记中心距离边界半个标记大小
float markerSizePx = config.getMarkerSize() * density;
float safeTopY = contentTop + markerSizePx * 0.5f;    // 标记贴着顶部边界
float safeBottomY = contentBottom - markerSizePx * 0.5f; // 标记贴着底部边界
```

**优化效果**：
- ✅ 从原来的 `1.5f` 边距降低到 `0.5f`
- ✅ 标记真正贴着图表边界显示
- ✅ 最大化利用可视空间
- ✅ 虚线长度调整更加精确

### 压缩检测阈值
```java
// 允许1像素的误差范围
boolean isCompressed = Math.abs(position.actualLineLength - originalLineLength) > 1f;
```

## ✅ 优势

### 用户体验
- ✅ 标记始终保持在可视区域内
- ✅ 虚线长度自动适应可用空间
- ✅ 压缩状态有清晰的视觉反馈
- ✅ 不会出现标记"消失"的情况

### 技术实现
- ✅ 自动边界检测和约束
- ✅ 动态长度计算
- ✅ 多种标记类型支持
- ✅ 性能优化（只在必要时调整）

### 视觉效果
- ✅ 保持标记与K线的逻辑关联
- ✅ 压缩状态有明显区分
- ✅ 颜色和样式一致性
- ✅ 支持调试和监控

## 🚀 扩展可能

### 未来优化方向
1. **智能重排**：当多个标记重叠时，自动重新排列位置
2. **渐变压缩**：根据压缩程度调整视觉效果强度
3. **用户配置**：允许用户自定义压缩阈值和视觉效果
4. **动画过渡**：在虚线长度调整时添加平滑动画

现在标记系统具备了完全的边界适应能力，确保在任何缩放级别下都能提供最佳的用户体验！📏✨ 