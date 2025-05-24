package com.alex.mpchart.marker.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alex.mpchart.marker.data.model.KLineEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

            // 生成日期序列 - 从3个月前开始，每天一条数据
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -100); // 从100天前开始
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            
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

                // 创建K线条目，使用Date对象作为时间标识
                KLineEntry entry = new KLineEntry(open, close, high, low, volume, (Date) calendar.getTime().clone());

                // 如果不是第一个K线，检测激增和陡降
                if (previousEntry != null) {
                    // 计算价格变化百分比
                    float changePercent = (close - previousEntry.close) / previousEntry.close;
                    // 计算成交量变化百分比
                    float volumeChangePercent = (volume - previousEntry.volume) / previousEntry.volume;

                    // 检测激增点位 - 价格上涨超过3%且成交量增长超过20%
                    if (changePercent > 0.03f && volumeChangePercent > 0.2f) {
                        entry.hasMarker = true;
                        entry.markerText = "↑";
                        entry.markerType = KLineEntry.MarkerType.UP_TRIANGLE;
                        Log.d("KLineRepository", "Generated UP_TRIANGLE marker at index " + i + ", change=" + String.format("%.2f%%", changePercent * 100) + ", volume=" + String.format("%.2f%%", volumeChangePercent * 100));
                    }
                    // 检测陡降点位 - 价格下跌超过3%且成交量增长超过20%
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

                // 日期向前推进一天
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            // 手动指定数字标记的位置和内容
            addNumberMarkers(list);

            // 统计生成的标记数量
            int totalMarkers = 0;
            for (KLineEntry entry : list) {
                if (entry.hasMarker) {
                    totalMarkers++;
                }
            }

            // 输出一些日期信息用于调试
            if (!list.isEmpty()) {
                SimpleDateFormat debugFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Log.d("KLineRepository", "First entry date: " + debugFormat.format(list.get(0).date));
                Log.d("KLineRepository", "Last entry date: " + debugFormat.format(list.get(list.size() - 1).date));
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
        // 定义要添加数字标记的具体位置和内容（基于数组索引）
        int[] markerPositions = {5, 15, 35, 60, 85};

        for (int i = 0; i < markerPositions.length; i++) {
            int position = markerPositions[i];
            int number = i + 1; // 数字从1开始

            // 检查位置是否有效，且该位置还没有其他标记
            if (position < entries.size() && !entries.get(position).hasMarker) {
                KLineEntry entry = entries.get(position);
                entry.hasMarker = true;
                entry.markerText = String.valueOf(number);
                entry.markerType = KLineEntry.MarkerType.NUMBER;
                SimpleDateFormat debugFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Log.d("KLineRepository", "Manually added NUMBER marker at position " + position + " with text: " + number + ", date: " + debugFormat.format(entry.date));
            }
        }
    }
} 