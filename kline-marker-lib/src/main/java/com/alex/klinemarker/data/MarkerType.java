package com.alex.klinemarker.data;

/**
 * 标记类型枚举
 * 定义标记的业务含义
 */
public enum MarkerType {
    /**
     * 无标记
     */
    NONE,

    /**
     * 买入信号
     */
    BUY,

    /**
     * 卖出信号
     */
    SELL,

    /**
     * 止损
     */
    STOP_LOSS,

    /**
     * 止盈
     */
    TAKE_PROFIT,

    /**
     * 重要事件
     */
    EVENT,

    /**
     * 数据激增
     */
    SURGE,

    /**
     * 数据骤降
     */
    PLUNGE,

    /**
     * 警告
     */
    WARNING,

    /**
     * 信息提示
     */
    INFO,

    /**
     * 自定义类型
     */
    CUSTOM
} 