package com.alex.mpchart.marker.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alex.mpchart.marker.data.model.KLineEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KLineRepository {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<KLineEntry>> getKLineData() {
        MutableLiveData<List<KLineEntry>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            // 模拟网络延迟
            try {
                Thread.sleep(1000 + (long) (Math.random() * 1000)); // 1-2秒的随机延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            List<KLineEntry> list = new ArrayList<>();
            // 使用随机起始价格，模拟不同的数据
            float base = 90f + (float) (Math.random() * 20); // 90-110之间的随机起始价格
            KLineEntry previousEntry = null;
            
            for (int i = 0; i < 100; i++) {
                float open = base + (float) (Math.random() * 10 - 5);
                float close = open + (float) (Math.random() * 10 - 5);
                float high = Math.max(open, close) + (float) (Math.random() * 5);
                float low = Math.min(open, close) - (float) (Math.random() * 5);
                float volume = (float) (Math.random() * 1000 + 500);
                KLineEntry entry = new KLineEntry(i, open, close, high, low, volume);

                // 如果不是第一个K线，检测激增和陡降
                if (previousEntry != null) {
                    float changePercent = (close - previousEntry.close) / previousEntry.close;
                    float volumeChangePercent = (volume - previousEntry.volume) / previousEntry.volume;

                    // 检测数据激增：涨幅超过8%且成交量增加50%以上
                    if (changePercent > 0.08f && volumeChangePercent > 0.5f) {
                        entry.hasMarker = true;
                        entry.markerText = "↑";
                        entry.markerType = KLineEntry.MarkerType.UP_TRIANGLE;
                    }
                    // 检测数据陡降：跌幅超过8%且成交量增加50%以上
                    else if (changePercent < -0.08f && volumeChangePercent > 0.5f) {
                        entry.hasMarker = true;
                        entry.markerText = "↓";
                        entry.markerType = KLineEntry.MarkerType.DOWN_TRIANGLE;
                    }
                    // 其他原有标记逻辑（只在没有三角标记时才添加）
                    else {
                        // 1. 价格标记 - 例如接近7.58的点
                        if (Math.abs(close - 7.58) < 0.1) {
                            entry.hasMarker = true;
                            entry.markerText = "7.6"; // 简化数字显示
                            entry.markerType = KLineEntry.MarkerType.PRICE;
                        }

                        // 2. 买入点标记 - 例如当收盘价比开盘价高5%以上
                        else if (close > open * 1.05) {
                            entry.hasMarker = true;
                            entry.markerText = "B";
                            entry.markerType = KLineEntry.MarkerType.BUY;
                        }

                        // 3. 卖出点标记 - 例如当收盘价比开盘价低5%以上
                        else if (close < open * 0.95) {
                            entry.hasMarker = true;
                            entry.markerText = "S";
                            entry.markerType = KLineEntry.MarkerType.SELL;
                        }

                        // 4. 数字标记 - 每10个点标记一个数字
                        else if (i % 10 == 0) {
                            entry.hasMarker = true;
                            entry.markerText = String.valueOf(i / 10);
                            entry.markerType = KLineEntry.MarkerType.NUMBER;
                        }
                    }
                } else {
                    // 第一个K线只检查其他标记
                    // 数字标记 - 每10个点标记一个数字
                    if (i % 10 == 0) {
                        entry.hasMarker = true;
                        entry.markerText = String.valueOf(i / 10);
                        entry.markerType = KLineEntry.MarkerType.NUMBER;
                    }
                }
                
                list.add(entry);
                previousEntry = entry;
                base = close;
            }
            liveData.postValue(list);
        });
        return liveData;
    }
} 