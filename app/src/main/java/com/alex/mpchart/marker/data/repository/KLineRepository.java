package com.alex.mpchart.marker.data.repository;

import android.util.Log;

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
            // 使用固定起始价格，确保有明显的趋势
            float base = 100f;
            KLineEntry previousEntry = null;
            
            for (int i = 0; i < 100; i++) {
                float open, close, high, low;

                // 创建一些明显的趋势区间用于测试
                if (i >= 10 && i <= 15) {
                    // 连续上涨区间
                    open = base;
                    close = base + 2f + (float) (Math.random() * 1); // 确保上涨
                    high = close + (float) (Math.random() * 1);
                    low = open - (float) (Math.random() * 0.5);
                    base = close;
                } else if (i >= 25 && i <= 30) {
                    // 连续下跌区间
                    open = base;
                    close = base - 2f - (float) (Math.random() * 1); // 确保下跌
                    high = open + (float) (Math.random() * 0.5);
                    low = close - (float) (Math.random() * 1);
                    base = close;
                } else if (i >= 50 && i <= 56) {
                    // 另一个连续上涨区间
                    open = base;
                    close = base + 1.5f + (float) (Math.random() * 1); // 确保上涨
                    high = close + (float) (Math.random() * 1);
                    low = open - (float) (Math.random() * 0.5);
                    base = close;
                } else {
                    // 随机变化
                    open = base + (float) (Math.random() * 4 - 2);
                    close = open + (float) (Math.random() * 4 - 2);
                    high = Math.max(open, close) + (float) (Math.random() * 2);
                    low = Math.min(open, close) - (float) (Math.random() * 2);
                    base = close;
                }
                
                float volume = (float) (Math.random() * 1000 + 500);
                KLineEntry entry = new KLineEntry(i, open, close, high, low, volume);

                // 如果不是第一个K线，检测激增和陡降
                if (previousEntry != null) {
                    float changePercent = (close - previousEntry.close) / previousEntry.close;
                    float volumeChangePercent = (volume - previousEntry.volume) / previousEntry.volume;

                    // 检测数据激增：涨幅超过3%且成交量增加20%以上（降低门槛）
                    if (changePercent > 0.03f && volumeChangePercent > 0.2f) {
                        entry.hasMarker = true;
                        entry.markerText = "↑";
                        entry.markerType = KLineEntry.MarkerType.UP_TRIANGLE;
                        Log.d("KLineRepository", "Generated UP_TRIANGLE marker at index " + i + ", change=" + String.format("%.2f%%", changePercent * 100) + ", volume=" + String.format("%.2f%%", volumeChangePercent * 100));
                    }
                    // 检测数据陡降：跌幅超过3%且成交量增加20%以上（降低门槛）
                    else if (changePercent < -0.03f && volumeChangePercent > 0.2f) {
                        entry.hasMarker = true;
                        entry.markerText = "↓";
                        entry.markerType = KLineEntry.MarkerType.DOWN_TRIANGLE;
                        Log.d("KLineRepository", "Generated DOWN_TRIANGLE marker at index " + i + ", change=" + String.format("%.2f%%", changePercent * 100) + ", volume=" + String.format("%.2f%%", volumeChangePercent * 100));
                    }
                    // 其他原有标记逻辑（只在没有三角标记时才添加）
                    else {
                        // 买入点标记 - 降低门槛到2%
                        if (close > open * 1.02) {
                            entry.hasMarker = true;
                            entry.markerText = "B";
                            entry.markerType = KLineEntry.MarkerType.BUY;
                            Log.d("KLineRepository", "Generated BUY marker at index " + i + ", open=" + open + ", close=" + close);
                        }

                        // 卖出点标记 - 降低门槛到2%
                        else if (close < open * 0.98) {
                            entry.hasMarker = true;
                            entry.markerText = "S";
                            entry.markerType = KLineEntry.MarkerType.SELL;
                            Log.d("KLineRepository", "Generated SELL marker at index " + i + ", open=" + open + ", close=" + close);
                        }
                    }
                } else {
                    // 第一个K线不进行自动标记检测
                }
                
                list.add(entry);
                previousEntry = entry;
            }

            // 手动指定数字标记的位置和内容
            addNumberMarkers(list);

            // 检测连续趋势区间
            detectTrendRegions(list);

            // 统计生成的标记数量
            int totalMarkers = 0;
            for (KLineEntry entry : list) {
                if (entry.hasMarker) {
                    totalMarkers++;
                }
            }
            Log.d("KLineRepository", "Data generation completed. Total markers generated: " + totalMarkers + " out of " + list.size() + " entries");
            
            liveData.postValue(list);
        });
        return liveData;
    }

    /**
     * 手动添加数字标记
     *
     * @param entries K线数据列表
     */
    private void addNumberMarkers(List<KLineEntry> entries) {
        // 定义要添加数字标记的具体位置和内容
        int[][] numberMarkers = {
                {5, 1},    // 在索引5处显示数字1
                {15, 2},   // 在索引15处显示数字2
                {35, 3},   // 在索引35处显示数字3
                {60, 4},   // 在索引60处显示数字4
                {85, 5}    // 在索引85处显示数字5
        };

        for (int[] marker : numberMarkers) {
            int index = marker[0];
            int number = marker[1];

            // 检查索引是否有效，且该位置还没有其他标记
            if (index < entries.size() && !entries.get(index).hasMarker) {
                KLineEntry entry = entries.get(index);
                entry.hasMarker = true;
                entry.markerText = String.valueOf(number);
                entry.markerType = KLineEntry.MarkerType.NUMBER;
                Log.d("KLineRepository", "Manually added NUMBER marker at index " + index + " with text: " + number);
            }
        }
    }

    /**
     * 检测连续趋势区间
     *
     * @param entries K线数据列表
     */
    private void detectTrendRegions(List<KLineEntry> entries) {
        if (entries.size() < 3) return; // 至少需要3个K线才能形成趋势

        int currentRegionId = 0;
        int consecutiveRising = 0;
        int consecutiveFalling = 0;
        int trendStartIndex = -1;

        for (int i = 1; i < entries.size(); i++) {
            KLineEntry current = entries.get(i);
            KLineEntry previous = entries.get(i - 1);

            boolean isRising = current.close > previous.close;
            boolean isFalling = current.close < previous.close;

            if (isRising) {
                // 如果之前有下跌趋势，先结束下跌趋势
                if (consecutiveFalling >= 3) {
                    finalizeTrendRegion(entries, trendStartIndex, i - 1,
                            KLineEntry.TrendType.FALLING, currentRegionId++);
                }

                consecutiveFalling = 0;
                consecutiveRising++;

                if (consecutiveRising == 1) {
                    trendStartIndex = i - 1; // 记录趋势开始位置（包括前一个K线）
                }

            } else if (isFalling) {
                // 如果之前有上涨趋势，先结束上涨趋势
                if (consecutiveRising >= 3) {
                    finalizeTrendRegion(entries, trendStartIndex, i - 1,
                            KLineEntry.TrendType.RISING, currentRegionId++);
                }

                consecutiveRising = 0;
                consecutiveFalling++;

                if (consecutiveFalling == 1) {
                    trendStartIndex = i - 1; // 记录趋势开始位置（包括前一个K线）
                }

            } else {
                // 收盘价相等，中断趋势
                if (consecutiveRising >= 3) {
                    finalizeTrendRegion(entries, trendStartIndex, i - 1,
                            KLineEntry.TrendType.RISING, currentRegionId++);
                } else if (consecutiveFalling >= 3) {
                    finalizeTrendRegion(entries, trendStartIndex, i - 1,
                            KLineEntry.TrendType.FALLING, currentRegionId++);
                }

                consecutiveRising = 0;
                consecutiveFalling = 0;
                trendStartIndex = -1;
            }
        }

        // 处理最后一个趋势区间
        if (consecutiveRising >= 3) {
            finalizeTrendRegion(entries, trendStartIndex, entries.size() - 1,
                    KLineEntry.TrendType.RISING, currentRegionId);
        } else if (consecutiveFalling >= 3) {
            finalizeTrendRegion(entries, trendStartIndex, entries.size() - 1,
                    KLineEntry.TrendType.FALLING, currentRegionId);
        }
    }

    /**
     * 完成趋势区间的标记
     */
    private void finalizeTrendRegion(List<KLineEntry> entries, int startIndex, int endIndex,
                                     KLineEntry.TrendType trendType, int regionId) {
        if (startIndex < 0 || endIndex < startIndex || endIndex >= entries.size()) {
            return;
        }

        for (int i = startIndex; i <= endIndex; i++) {
            KLineEntry entry = entries.get(i);
            entry.isInTrendRegion = true;
            entry.trendType = trendType;
            entry.regionId = regionId;
            entry.isRegionStart = (i == startIndex);
            entry.isRegionEnd = (i == endIndex);
        }
    }
} 