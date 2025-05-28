# K线图趋势区间性能优化

## 问题分析

在添加趋势区间功能后，K线图出现滑动不流畅、断触或卡顿的问题，主要原因包括：

1. **过度的日志输出**：调试日志在每次绘制时都会输出，影响性能
2. **复杂计算在主线程**：贝塞尔曲线、平滑算法、渐变计算等
3. **频繁的数据查找**：每次绘制都要遍历查找趋势区间内的K线数据
4. **对象重复创建**：Path、Paint等对象频繁创建和销毁
5. **不必要的坐标转换**：重复计算屏幕坐标

## 优化措施

### 1. 缓存机制

- **数据缓存**：缓存趋势区间内的K线数据，避免重复查找
- **路径缓存**：缓存绘制路径，避免重复计算
- **智能失效**：只在必要时重新计算缓存

### 2. 性能配置

- **性能模式**：`enablePerformanceMode(true)` 自动禁用复杂渲染
- **区间限制**：`maxVisibleRegions(5)` 限制同时绘制的区间数量
- **简化渲染**：禁用贝塞尔曲线、渐变、平滑等复杂特性

### 3. 绘制优化

- **帧率控制**：限制重绘频率到60fps
- **异常处理**：防止渲染错误影响主图表
- **日志控制**：通过DEBUG标志控制日志输出

### 4. 内存优化

- **对象复用**：复用SimpleDateFormat等对象
- **及时清理**：在数据变更时清理缓存

## 配置示例

### 高性能配置（推荐）

```java
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
    .risingColor(Color.parseColor("#00AA00"))
    .fallingColor(Color.parseColor("#FF4444"))
    .neutralColor(Color.parseColor("#4285F4"))
    .topAlpha(0.15f)
    .bottomAlpha(0.03f)
    .offsetDp(4f)
    .enablePerformanceMode(true) // 启用性能模式
    .maxVisibleRegions(5) // 限制区间数量
    .build();
```

### 平衡配置

```java
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
    .risingColor(Color.parseColor("#00AA00"))
    .fallingColor(Color.parseColor("#FF4444"))
    .neutralColor(Color.parseColor("#4285F4"))
    .topAlpha(0.20f)
    .bottomAlpha(0.05f)
    .offsetDp(4f)
    .enableBezierCurve(false)
    .enableGradient(false)
    .enableSmoothing(true)
    .smoothWindowSize(2)
    .maxVisibleRegions(8)
    .build();
```

### 高质量配置（性能较低）

```java
TrendRegionConfig trendConfig = new TrendRegionConfig.Builder()
    .risingColor(Color.parseColor("#00AA00"))
    .fallingColor(Color.parseColor("#FF4444"))
    .neutralColor(Color.parseColor("#4285F4"))
    .topAlpha(0.25f)
    .bottomAlpha(0.05f)
    .offsetDp(4f)
    .enableBezierCurve(true)
    .enableGradient(true)
    .enableSmoothing(true)
    .smoothWindowSize(3)
    .build();
```

## 性能测试

### 测试步骤

1. 运行应用，加载K线数据
2. 点击"添加趋势区间"按钮
3. 测试滑动流畅度：
    - 缓慢滑动：检查是否有卡顿
    - 快速滑动：检查是否有断触
    - 惯性滑动：检查是否流畅停止

### 性能指标

- **帧率**：目标 ≥ 50fps
- **触摸延迟**：< 16ms
- **内存使用**：稳定，无明显泄漏

### 调试技巧

1. 启用GPU渲染分析：开发者选项 > GPU渲染模式分析
2. 查看系统性能：设置 > 开发者选项 > 系统跟踪
3. 监控内存使用：Android Studio > Profiler

## 进一步优化建议

### 如果仍有性能问题

1. **减少区间数量**：`maxVisibleRegions(3)`
2. **降低透明度**：`topAlpha(0.1f)`
3. **简化数据**：减少K线数据点
4. **硬件加速**：确保启用硬件加速

### 自定义优化

1. 实现自定义的区间可见性检查
2. 使用异步计算复杂路径
3. 实现LOD（细节层次）渲染

## 故障排除

### 常见问题

1. **滑动仍然卡顿**：检查是否启用了性能模式
2. **趋势区间不显示**：检查区间数据和时间范围
3. **内存占用过高**：减少maxVisibleRegions值

### 日志检查

通过将TrendRegionRenderer中的DEBUG设为true来启用详细日志，但记住在发布时关闭。 