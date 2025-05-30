package com.alex.klinemarker.data;

/**
 * 标记形状枚举
 * 定义标记的基本形状
 */
public enum MarkerShape {
    /**
     * 矩形
     */
    RECTANGLE,

    /**
     * 圆形
     */
    CIRCLE,

    /**
     * 三角形（向上）
     */
    TRIANGLE_UP,

    /**
     * 三角形（向下）
     */
    TRIANGLE_DOWN,

    /**
     * 菱形
     */
    DIAMOND,

    /**
     * 五角星
     */
    STAR,

    /**
     * 圆点
     */
    DOT,

    /**
     * 十字
     */
    CROSS,

    /**
     * 箭头（向上）
     */
    ARROW_UP,

    /**
     * 箭头（向下）
     */
    ARROW_DOWN,

    /**
     * 自定义图标
     */
    CUSTOM_ICON,

    /**
     * 无形状（仅文字）
     */
    NONE
} 