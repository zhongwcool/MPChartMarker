package com.alex.klinemarker.data;

/**
 * 虚线长度枚举
 * 用于控制标记与K线数据点之间的距离
 */
public enum LineLength {
    /**
     * 极短距离 - 标记紧贴K线数据点，虚线很短但仍连接
     */
    NONE,

    /**
     * 短距离 - 标记与K线数据点距离较短
     */
    SHORT,

    /**
     * 中等距离 - 标记与K线数据点距离适中（默认）
     */
    MEDIUM,

    /**
     * 长距离 - 标记与K线数据点距离较长
     */
    LONG,

    /**
     * 超长距离 - 标记与K线数据点距离很长
     */
    EXTRA_LONG
} 