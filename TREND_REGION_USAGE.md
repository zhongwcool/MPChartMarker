# 趋势区间绘制功能使用说明

## 概述

`TrendRegionDrawer` 类现在支持根据指定的时间范围绘制趋势背景。图表最新日期的数据显示在最右侧，如果
`end` 为 `null`，则从 `start` 位置开始一直绘制到最后。

## 数据源格式

支持以下JSON格式的数据源：

```json
{
  "items": [
    {
      "updated_at": "2025-05-22T07:09:33.289230Z",
      "start": "2025-05-15",
      "end": null,
      "size": 2
    },
    {
      "updated_at": "2025-05-22T06:46:10.313136Z",
      "start": "2025-03-19",
      "end": "2025-03-24",
      "size": 3
    }
  ]
}
```

### 字段说明

- `start`: 趋势区间的起始日期，格式为 `yyyy-MM-dd`
- `end`: 趋势区间的结束日期，格式为 `yyyy-MM-dd`。如果为 `null`，表示从起始日期一直到最后
- `size`: 趋势强度（此字段被忽略，不影响显示效果）
- `updated_at`: 更新时间戳

### 显示效果

所有趋势区间使用统一的蓝色背景，透明度为 25%，带有从上到下的渐变效果。

## 使用方法

### 方法1：使用JSON数据

```java
// 在HomeFragment或其他地方
String jsonData = "{\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T07:09:33.289230Z\",\n" +
                "      \"start\": \"2025-05-15\",\n" +
                "      \"end\": null,\n" +
                "      \"size\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T06:46:10.313136Z\",\n" +
                "      \"start\": \"2025-03-19\",\n" +
                "      \"end\": \"2025-03-24\",\n" +
                "      \"size\": 3\n" +
                "    }\n" +
                "  ]\n" +
                "}";

// 设置趋势区间
trendRegionDrawer.

setTrendRegionsFromJson(jsonData);
```

### 方法2：直接创建TrendRegion对象

```java
List<TrendRegion> regions = new ArrayList<>();

// 创建趋势区间
regions.

add(new TrendRegion("2025-05-15", null,2,"2025-05-22T07:09:33.289230Z"));
        regions.

add(new TrendRegion("2025-03-19", "2025-03-24",3,"2025-05-22T06:46:10.313136Z"));

// 设置趋势区间
        trendRegionDrawer.

setTrendRegions(regions);
```

## 核心类说明

### TrendRegion

趋势区间数据模型，包含起始日期、结束日期、强度等信息。

### TrendRegionDrawer

趋势区间绘制器，负责在图表上绘制趋势背景。

主要方法：

- `setTrendRegions(List<TrendRegion> regions)`: 直接设置趋势区间列表
- `setTrendRegionsFromJson(String jsonData)`: 从JSON数据设置趋势区间
- `drawTrendRegions(Canvas canvas)`: 绘制趋势区间背景

### TrendRegionParser

JSON数据解析工具，负责将JSON格式的数据转换为TrendRegion对象列表。

### KLineEntry

K线数据模型，现在包含日期字段 `dateStr` 和 `date`，用于与趋势区间进行时间匹配。

## 注意事项

1. **日期格式**: 所有日期必须使用 `yyyy-MM-dd` 格式
2. **时间匹配**: 系统会根据K线数据的日期字段与趋势区间的时间范围进行匹配
3. **图表布局**: 图表最新日期的数据显示在最右侧
4. **渐变效果**: 背景使用从上到下的渐变效果，顶部颜色最深，底部最淡
5. **性能优化**: 只绘制当前可见区域内的趋势背景

## 示例效果

- 从2025-05-15开始到最后的蓝色背景
- 从2025-03-19到2025-03-24的蓝色背景
- 所有背景都使用统一的蓝色渐变效果，覆盖整个图表高度

## 扩展功能

如需添加更多功能，可以：

1. 在 `TrendRegion` 类中添加更多属性
2. 在 `TrendRegionDrawer` 中自定义绘制逻辑
3. 在 `TrendRegionParser` 中支持更多JSON格式 