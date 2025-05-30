package com.alex.klinemarker.data;

/**
 * 标记位置枚举
 * 定义标记相对于K线的位置
 */
public enum MarkerPosition {
    /**
     * K线上方
     */
    ABOVE,

    /**
     * K线下方
     */
    BELOW,

    /**
     * 自动选择（根据K线位置自动决定）
     */
    AUTO
} 