package com.alex.mpchart.sample;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * K线数据模型
 * 包含开高低收价格和成交量信息
 */
public class KLineData {
    private Date date;      // 日期
    private float open;     // 开盘价
    private float high;     // 最高价
    private float low;      // 最低价
    private float close;    // 收盘价
    private float volume;   // 成交量
    private int index = 0;  // 添加索引字段

    public KLineData() {
    }

    public KLineData(Date date, float open, float high, float low, float close, float volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * 获取X轴坐标值（使用索引）
     */
    public float getXValue() {
        return (float) index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    /**
     * 判断是否为阳线
     */
    public boolean isRising() {
        return close > open;
    }

    /**
     * 格式化日期显示
     */
    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return format.format(date);
    }

    @Override
    public String toString() {
        return "KLineData{" +
                "date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
} 