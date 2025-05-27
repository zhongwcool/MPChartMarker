package com.alex.klinemarker.data;

import java.util.Date;

/**
 * K线标记数据模型
 * 用于定义在K线图上显示的标记信息
 */
public class MarkerData {

    /**
     * 标记类型枚举
     */
    public enum MarkerType {
        NONE,           // 无标记
        BUY,            // 买入标记
        SELL,           // 卖出标记
        NUMBER,         // 数字标记
        UP_TRIANGLE,    // 上三角，表示数据激增
        DOWN_TRIANGLE   // 下三角，表示数据陡降
    }

    private Date date;              // 标记日期
    private MarkerType type;        // 标记类型
    private String text;            // 标记显示文本
    private int color;              // 标记颜色（可选，使用默认颜色时传入0）
    private Object extraData;       // 额外数据，用于扩展

    public MarkerData() {
        this.type = MarkerType.NONE;
        this.text = "";
        this.color = 0;
    }

    public MarkerData(Date date, MarkerType type, String text) {
        this.date = date;
        this.type = type;
        this.text = text;
        this.color = 0;
    }

    public MarkerData(Date date, MarkerType type, String text, int color) {
        this.date = date;
        this.type = type;
        this.text = text;
        this.color = color;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MarkerType getType() {
        return type;
    }

    public void setType(MarkerType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    /**
     * 获取日期的x轴坐标值，用于图表显示
     */
    public float getXValue() {
        if (date == null) return 0;
        // 使用相对天数，以2024年1月1日为基准
        long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
        long daysSinceBase = (date.getTime() - baseTime) / (24 * 60 * 60 * 1000);
        return (float) daysSinceBase;
    }

    @Override
    public String toString() {
        return "MarkerData{" +
                "date=" + date +
                ", type=" + type +
                ", text='" + text + '\'' +
                ", color=" + color +
                '}';
    }
} 