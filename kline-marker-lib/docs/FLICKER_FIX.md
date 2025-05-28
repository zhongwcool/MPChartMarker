# 趋势区间闪烁问题修复

## 问题描述

在性能优化后，趋势区间在K线图滑动时出现闪烁甚至消失的现象。

## 根本原因分析

### 1. 缓存键冲突问题

**原问题**：`findEntriesInRegion` 方法只查找可见范围内的数据，导致相同的缓存键对应不同的数据集。

- 滑动到不同位置时，同一个区间的缓存键相同
- 但实际返回的数据不同（只包含当前可见范围内的数据）
- 导致缓存失效和数据不一致

### 2. 路径坐标缓存问题

**原问题**：缓存的Path对象包含绝对屏幕坐标，滑动后坐标系已变化。

- Path中的坐标是基于当时的视口位置计算的
- 滑动后视口改变，但使用的还是旧坐标
- 导致趋势区间显示在错误位置或完全不可见

### 3. 可见性检查缺陷

**原问题**：`isRegionVisible` 方法直接返回true，没有实际判断区间是否在可见范围内。

- 导致不必要的绘制操作
- 影响性能且可能引起显示问题

## 修复方案

### 1. 分离数据缓存和可见性筛选

```java
// 修复前：缓存可见范围内的数据（错误）
List<T> regionEntries = findEntriesInRegion(region, minTime, maxTime);

// 修复后：缓存所有区间数据，动态筛选可见部分
List<T> allRegionEntries = findAllEntriesInRegion(region); // 缓存完整数据
// 在绘制时动态筛选可见部分
List<T> visibleEntries = filterVisibleEntries(allRegionEntries, minTime, maxTime);
```

### 2. 实时计算路径坐标

```java
// 修复前：缓存Path对象（错误）
Path cachedPath = regionPathCache.get(regionKey);

// 修复后：每次绘制时重新计算路径
Path backgroundPath = createRegionPath(visibleEntries, contentBottom);
```

### 3. 实现真正的可见性检查

```java
// 修复前：总是返回true
private boolean isRegionVisible(TrendRegion region, float minTime, float maxTime) {
    return true; // 错误
}

// 修复后：基于时间范围的真实可见性检查
private boolean isRegionVisible(TrendRegion region, float minTime, float maxTime) {
    float regionStartX = getDateXValue(region.getStart());
    float regionEndX = region.getEnd() != null ? getDateXValue(region.getEnd()) : Float.MAX_VALUE;
    return !(regionEndX < minTime || regionStartX > maxTime);
}
```

### 4. 移除过度的帧率限制

```java
// 修复前：严格的帧率控制可能导致重要帧被跳过
if (currentTime - lastDrawTime < MIN_DRAW_INTERVAL) {
    super.drawData(c);
    return; // 可能跳过重要的绘制
}

// 修复后：正常绘制，让系统和硬件自然控制帧率
```

## 修复后的工作流程

1. **初始化阶段**：缓存每个趋势区间的完整K线数据
2. **可见性检查**：基于时间范围判断区间是否需要绘制
3. **动态筛选**：从缓存的完整数据中筛选出当前可见的部分
4. **实时绘制**：使用当前视口坐标重新计算并绘制路径

## 性能影响

### 优势

- ✅ 消除了闪烁和消失问题
- ✅ 数据缓存仍然有效（完整区间数据）
- ✅ 可见性检查减少不必要的计算
- ✅ 更稳定的渲染表现

### 权衡

- ⚠️ 路径需要每帧重新计算（但这是必需的）
- ⚠️ 增加了可见性筛选的开销（但很轻量）

## 验证方法

### 1. 日志验证

运行应用并查看日志输出：

```
D/TrendRegionRenderer: Region 2024-01-05 to 2024-01-20: startX=5.0, endX=20.0, minTime=10.0, maxTime=30.0, visible=true
D/TrendRegionRenderer: Drawing region 2024-01-05 to 2024-01-20: 8 visible entries out of 15 total
```

### 2. 功能验证

1. 加载K线数据并添加趋势区间
2. 缓慢滑动：检查趋势区间是否连续显示
3. 快速滑动：检查是否有闪烁现象
4. 边界滑动：检查区间进入/离开可视区域的表现

### 3. 性能验证

- 使用GPU Profile工具检查渲染性能
- 观察内存使用是否稳定
- 确认滑动流畅度

## 后续建议

### 1. 发布前清理

```java
private static final boolean DEBUG = false; // 关闭调试日志
```

### 2. 进一步优化

如果性能仍有问题，可以考虑：

- 使用更粗粒度的可见性检查
- 实现路径的简化算法
- 添加自适应质量控制

### 3. 监控机制

建议添加性能监控来跟踪：

- 平均绘制时间
- 趋势区间数量对性能的影响
- 用户设备的表现差异 