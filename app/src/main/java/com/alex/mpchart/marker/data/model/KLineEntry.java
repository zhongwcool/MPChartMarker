package com.alex.mpchart.marker.data.model;

import java.util.Date;

public class KLineEntry {
    public float open;
    public float close;
    public float high;
    public float low;
    public float volume;
    public boolean hasMarker;
    public String markerText;
    public MarkerType markerType;

    // 日期字段
    public Date date;           // 日期对象，用作x轴坐标

    // 区间背景相关字段
    public boolean isInTrendRegion;       // 是否在趋势区间内
    public TrendType trendType;           // 趋势类型
    public boolean isRegionStart;         // 是否是区间开始
    public boolean isRegionEnd;           // 是否是区间结束
    public int regionId;                  // 区间ID，用于标识同一个区间

    public KLineEntry(float open, float close, float high, float low, float volume, Date date) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.date = date;
        this.hasMarker = false;
        this.markerText = "";
        this.markerType = MarkerType.NONE;

        // 初始化区间背景字段
        this.isInTrendRegion = false;
        this.trendType = TrendType.NONE;
        this.isRegionStart = false;
        this.isRegionEnd = false;
        this.regionId = -1;
    }

    /**
     * 获取日期的x轴坐标值，用于图表显示
     * 为了图表显示，我们使用相对天数而不是绝对时间戳
     */
    public float getXValue() {
        if (date == null) return 0;
        // 使用相对天数，以2024年1月1日为基准
        long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
        long daysSinceBase = (date.getTime() - baseTime) / (24 * 60 * 60 * 1000);
        return (float) daysSinceBase;
    }

    /**
     * 获取原始时间戳
     */
    public long getTimeStamp() {
        return date != null ? date.getTime() : 0;
    }

    public enum MarkerType {
        NONE,
        BUY,
        SELL,
        NUMBER,
        UP_TRIANGLE,    // 上三角，表示数据激增
        DOWN_TRIANGLE   // 下三角，表示数据陡降
    }

    /**
     * 将数字类型转换为MarkerType枚举
     *
     * @param type 数字类型：0-数字标记，1-买入，2-卖出，3-上三角，4-下三角
     * @return 对应的MarkerType枚举值
     */
    public static MarkerType typeFromInt(int type) {
        switch (type) {
            case 0:
                return MarkerType.NUMBER;
            case 1:
                return MarkerType.BUY;
            case 2:
                return MarkerType.SELL;
            case 3:
                return MarkerType.UP_TRIANGLE;
            case 4:
                return MarkerType.DOWN_TRIANGLE;
            default:
                return MarkerType.NONE;
        }
    }

    public enum TrendType {
        NONE,
        RISING,    // 连续上涨
        FALLING    // 连续下跌
    }
} 