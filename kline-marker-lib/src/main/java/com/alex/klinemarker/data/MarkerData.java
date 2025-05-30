package com.alex.klinemarker.data;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * K线标记数据模型
 * 用于定义在K线图上显示的标记信息
 */
public class MarkerData {

    private Date date;              // 标记日期
    private MarkerType type;        // 标记类型（业务含义）
    private MarkerStyle style;      // 标记样式（视觉表现）
    private String text;            // 标记显示文本
    private int color;              // 标记颜色（可选，使用默认颜色时传入0）
    private int textColor;          // 文字颜色（可选）
    private float size;             // 标记大小（可选，0表示使用默认大小）
    private LineLength lineLength;  // 虚线长度类型
    private Drawable customIcon;    // 自定义图标（当style为CUSTOM_ICON时使用）
    private Object extraData;       // 额外数据，用于扩展

    public MarkerData() {
        this.type = MarkerType.NONE;
        this.style = MarkerStyle.RECTANGLE_TEXT;
        this.text = "";
        this.color = 0;
        this.textColor = 0;
        this.size = 0;
        this.lineLength = LineLength.MEDIUM; // 默认中等长度
    }

    public MarkerData(Date date, MarkerType type, MarkerStyle style, String text) {
        this.date = date;
        this.type = type;
        this.style = style;
        this.text = text;
        this.color = 0;
        this.textColor = 0;
        this.size = 0;
        this.lineLength = LineLength.MEDIUM; // 默认中等长度
    }

    public MarkerData(Date date, MarkerType type, MarkerStyle style, String text, int color) {
        this.date = date;
        this.type = type;
        this.style = style;
        this.text = text;
        this.color = color;
        this.textColor = 0;
        this.size = 0;
        this.lineLength = LineLength.MEDIUM; // 默认中等长度
    }

    public MarkerData(Date date, MarkerType type, MarkerStyle style, String text, int color, LineLength lineLength) {
        this.date = date;
        this.type = type;
        this.style = style;
        this.text = text;
        this.color = color;
        this.textColor = 0;
        this.size = 0;
        this.lineLength = lineLength;
    }

    // 便捷构造方法，用于创建常见的标记
    public static MarkerData createBuyMarker(Date date, String text) {
        return new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, text, 0, LineLength.SHORT);
    }

    public static MarkerData createSellMarker(Date date, String text) {
        return new MarkerData(date, MarkerType.SELL, MarkerStyle.RECTANGLE_TEXT, text, 0, LineLength.SHORT);
    }

    public static MarkerData createSurgeMarker(Date date) {
        return new MarkerData(date, MarkerType.SURGE, MarkerStyle.TRIANGLE_UP, "", 0, LineLength.SHORT);
    }

    public static MarkerData createPlungeMarker(Date date) {
        return new MarkerData(date, MarkerType.PLUNGE, MarkerStyle.TRIANGLE_DOWN, "", 0, LineLength.SHORT);
    }

    public static MarkerData createEventMarker(Date date, String text) {
        return new MarkerData(date, MarkerType.EVENT, MarkerStyle.CIRCLE_TEXT, text, 0, LineLength.LONG);
    }

    // 新增便捷方法，支持指定虚线长度
    public static MarkerData createBuyMarker(Date date, String text, LineLength lineLength) {
        return new MarkerData(date, MarkerType.BUY, MarkerStyle.RECTANGLE_TEXT, text, 0, lineLength);
    }

    public static MarkerData createSellMarker(Date date, String text, LineLength lineLength) {
        return new MarkerData(date, MarkerType.SELL, MarkerStyle.RECTANGLE_TEXT, text, 0, lineLength);
    }

    public static MarkerData createTextMarker(Date date, String text, LineLength lineLength) {
        return new MarkerData(date, MarkerType.INFO, MarkerStyle.TEXT_ONLY, text, 0, lineLength);
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

    public MarkerStyle getStyle() {
        return style;
    }

    public void setStyle(MarkerStyle style) {
        this.style = style;
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

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public LineLength getLineLength() {
        return lineLength;
    }

    public void setLineLength(LineLength lineLength) {
        this.lineLength = lineLength;
    }

    public Drawable getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(Drawable customIcon) {
        this.customIcon = customIcon;
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
                ", style=" + style +
                ", text='" + text + '\'' +
                ", color=" + color +
                ", lineLength=" + lineLength +
                ", size=" + size +
                '}';
    }
} 