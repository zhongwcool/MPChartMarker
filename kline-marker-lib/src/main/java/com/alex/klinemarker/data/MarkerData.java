package com.alex.klinemarker.data;

import java.util.Date;

/**
 * K线标记数据模型
 * 用于定义在K线图上显示的标记信息
 */
public class MarkerData {

    private Date date;              // 标记日期
    private String text;            // 标记显示文本
    private MarkerConfig config;    // 标记配置（包含所有视觉属性）
    private Object extraData;       // 额外数据，用于扩展

    /**
     * 默认构造函数
     */
    public MarkerData() {
        this.text = "";
        this.config = new MarkerConfig();
    }

    /**
     * 基本构造函数
     *
     * @param date   日期
     * @param text   文本
     * @param config 标记配置
     */
    public MarkerData(Date date, String text, MarkerConfig config) {
        this.date = date;
        this.text = text;
        this.config = config != null ? config.copy() : new MarkerConfig(); // 复制配置，避免共享引用
    }

    // 便捷构造方法，使用预定义配置

    /**
     * 创建买入标记
     */
    public static MarkerData createBuyMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.buy());
    }

    /**
     * 创建卖出标记
     */
    public static MarkerData createSellMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.sell());
    }

    /**
     * 创建警告标记
     */
    public static MarkerData createWarningMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.warning());
    }

    /**
     * 创建信息标记
     */
    public static MarkerData createInfoMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.info());
    }

    /**
     * 创建事件标记
     */
    public static MarkerData createEventMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.event());
    }

    /**
     * 创建止损标记
     */
    public static MarkerData createStopLossMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.stopLoss());
    }

    /**
     * 创建止盈标记
     */
    public static MarkerData createTakeProfitMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.takeProfit());
    }

    /**
     * 创建纯文本标记
     */
    public static MarkerData createTextMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.textOnly());
    }

    /**
     * 创建重要标记
     */
    public static MarkerData createImportantMarker(Date date, String text) {
        return new MarkerData(date, text, MarkerPresets.important());
    }

    /**
     * 创建简单点标记（不显示文字）
     */
    public static MarkerData createDotMarker(Date date) {
        return new MarkerData(date, "", MarkerPresets.dot());
    }

    /**
     * 创建自定义标记
     *
     * @param date         日期
     * @param text         文本
     * @param customConfig 自定义配置
     */
    public static MarkerData createCustomMarker(Date date, String text, MarkerConfig customConfig) {
        return new MarkerData(date, text, customConfig);
    }

    /**
     * 基于预设创建自定义标记
     * 示例：MarkerData.createCustomMarker(date, "Custom", MarkerPresets.customize(MarkerPresets.info()).backgroundColor(0xFF00FF00).build())
     */
    public static MarkerData createCustomMarker(Date date, String text, MarkerConfig.Builder configBuilder) {
        return new MarkerData(date, text, configBuilder.build());
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MarkerConfig getConfig() {
        return config;
    }

    public void setConfig(MarkerConfig config) {
        this.config = config;
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
                ", text='" + text + '\'' +
                ", config=" + config +
                ", extraData=" + extraData +
                '}';
    }
} 