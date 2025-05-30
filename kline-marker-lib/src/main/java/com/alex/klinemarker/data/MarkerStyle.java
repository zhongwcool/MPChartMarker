package com.alex.klinemarker.data;

/**
 * 标记样式枚举
 * 定义标记的视觉样式类型
 */
public enum MarkerStyle {
    /**
     * 矩形背景 + 文字（如BUY/SELL）
     */
    RECTANGLE_TEXT,

    /**
     * 圆形背景 + 文字
     */
    CIRCLE_TEXT,

    /**
     * 上三角形
     */
    TRIANGLE_UP,

    /**
     * 下三角形
     */
    TRIANGLE_DOWN,

    /**
     * 纯文字（带引出线）
     */
    TEXT_ONLY,

    /**
     * 菱形背景 + 文字
     */
    DIAMOND_TEXT,

    /**
     * 五角星
     */
    STAR,

    /**
     * 圆点
     */
    DOT,

    /**
     * 十字标记
     */
    CROSS,

    /**
     * 箭头向上
     */
    ARROW_UP,

    /**
     * 箭头向下
     */
    ARROW_DOWN,

    /**
     * 自定义图标（支持Drawable）
     */
    CUSTOM_ICON
} 