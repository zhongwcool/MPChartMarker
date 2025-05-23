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
    }

    public enum MarkerType {
        NONE,
        BUY,
        SELL,
        PRICE,
        NUMBER,
        UP_TRIANGLE,    // 上三角，表示数据激增
        DOWN_TRIANGLE   // 下三角，表示数据陡降
    }
} 