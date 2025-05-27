package com.alex.klinemarker.utils;

import com.alex.klinemarker.data.KLineDataAdapter;

import java.util.Date;

/**
 * 默认K线数据适配器示例
 * 展示如何为常见的K线数据格式创建适配器
 */
public class DefaultKLineDataAdapter implements KLineDataAdapter<DefaultKLineDataAdapter.DefaultKLineData> {

    @Override
    public Date getDate(DefaultKLineData klineData) {
        return klineData.date;
    }

    @Override
    public float getOpen(DefaultKLineData klineData) {
        return klineData.open;
    }

    @Override
    public float getClose(DefaultKLineData klineData) {
        return klineData.close;
    }

    @Override
    public float getHigh(DefaultKLineData klineData) {
        return klineData.high;
    }

    @Override
    public float getLow(DefaultKLineData klineData) {
        return klineData.low;
    }

    @Override
    public float getVolume(DefaultKLineData klineData) {
        return klineData.volume;
    }

    @Override
    public float getXValue(DefaultKLineData klineData) {
        if (klineData.date == null) return 0;
        // 使用相对天数，以2024年1月1日为基准
        long baseTime = 1704067200000L; // 2024-01-01 00:00:00 UTC
        long daysSinceBase = (klineData.date.getTime() - baseTime) / (24 * 60 * 60 * 1000);
        return (float) daysSinceBase;
    }

    /**
     * 默认K线数据结构示例
     * 其他项目可以参考这个结构创建自己的数据类
     */
    public static class DefaultKLineData {
        public Date date;
        public float open;
        public float close;
        public float high;
        public float low;
        public float volume;

        public DefaultKLineData() {
        }

        public DefaultKLineData(Date date, float open, float close, float high, float low, float volume) {
            this.date = date;
            this.open = open;
            this.close = close;
            this.high = high;
            this.low = low;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return "DefaultKLineData{" +
                    "date=" + date +
                    ", open=" + open +
                    ", close=" + close +
                    ", high=" + high +
                    ", low=" + low +
                    ", volume=" + volume +
                    '}';
        }
    }
} 