package com.alex.klinemarker.data;

import java.util.Date;

/**
 * K线数据适配器接口
 * 用于适配不同格式的K线数据，使标记库能够与各种K线数据结构兼容
 */
public interface KLineDataAdapter<T> {

    /**
     * 获取K线数据的日期
     *
     * @param klineData K线数据对象
     * @return 日期
     */
    Date getDate(T klineData);

    /**
     * 获取K线数据的开盘价
     *
     * @param klineData K线数据对象
     * @return 开盘价
     */
    float getOpen(T klineData);

    /**
     * 获取K线数据的收盘价
     *
     * @param klineData K线数据对象
     * @return 收盘价
     */
    float getClose(T klineData);

    /**
     * 获取K线数据的最高价
     *
     * @param klineData K线数据对象
     * @return 最高价
     */
    float getHigh(T klineData);

    /**
     * 获取K线数据的最低价
     *
     * @param klineData K线数据对象
     * @return 最低价
     */
    float getLow(T klineData);

    /**
     * 获取K线数据的成交量
     *
     * @param klineData K线数据对象
     * @return 成交量
     */
    float getVolume(T klineData);

    /**
     * 获取K线数据的X轴坐标值
     *
     * @param klineData K线数据对象
     * @return X轴坐标值
     */
    float getXValue(T klineData);
} 