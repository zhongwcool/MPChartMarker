package com.alex.mpchart.marker.data.model;

public class KLineEntry {
    public int index;
    public float open;
    public float close;
    public float high;
    public float low;
    public float volume;
    public boolean hasMarker;
    public String markerText;
    public MarkerType markerType;

    // 区间背景相关字段
    public boolean isInTrendRegion;       // 是否在趋势区间内
    public TrendType trendType;           // 趋势类型
    public boolean isRegionStart;         // 是否是区间开始
    public boolean isRegionEnd;           // 是否是区间结束
    public int regionId;                  // 区间ID，用于标识同一个区间

    public KLineEntry(int index, float open, float close, float high, float low, float volume) {
        this.index = index;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
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

    public enum MarkerType {
        NONE,
        BUY,
        SELL,
        NUMBER,
        UP_TRIANGLE,    // 上三角，表示数据激增
        DOWN_TRIANGLE   // 下三角，表示数据陡降
    }

    public enum TrendType {
        NONE,
        RISING,    // 连续上涨
        FALLING    // 连续下跌
    }
} 