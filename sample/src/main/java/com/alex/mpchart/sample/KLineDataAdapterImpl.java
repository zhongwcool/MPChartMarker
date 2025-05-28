package com.alex.mpchart.sample;

import com.alex.klinemarker.data.KLineDataAdapter;

import java.util.Date;

/**
 * K线数据适配器实现
 * 将我们的KLineData适配到kline-marker-lib库中
 */
public class KLineDataAdapterImpl implements KLineDataAdapter<KLineData> {

    @Override
    public Date getDate(KLineData klineData) {
        return klineData.getDate();
    }

    @Override
    public float getOpen(KLineData klineData) {
        return klineData.getOpen();
    }

    @Override
    public float getClose(KLineData klineData) {
        return klineData.getClose();
    }

    @Override
    public float getHigh(KLineData klineData) {
        return klineData.getHigh();
    }

    @Override
    public float getLow(KLineData klineData) {
        return klineData.getLow();
    }

    @Override
    public float getVolume(KLineData klineData) {
        return klineData.getVolume();
    }

    @Override
    public float getXValue(KLineData klineData) {
        return klineData.getXValue();
    }
} 