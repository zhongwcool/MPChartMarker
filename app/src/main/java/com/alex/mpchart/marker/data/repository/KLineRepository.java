package com.alex.mpchart.marker.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alex.mpchart.marker.data.model.KLineEntry;
import com.alex.mpchart.marker.data.model.MarkerResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KLineRepository {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MarkerDataService markerDataService = new MarkerDataService();

    public LiveData<List<KLineEntry>> getKLineData() {
        MutableLiveData<List<KLineEntry>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            // 模拟网络延迟
            try {
                Thread.sleep(1000 + (long) (Math.random() * 1000)); // 1-2秒的随机延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 生成基础K线数据
            List<KLineEntry> list = generateBaseKLineData();

            // 使用CountDownLatch等待标记数据获取完成
            CountDownLatch latch = new CountDownLatch(1);

            // 获取外部标记数据并应用到K线数据中
            markerDataService.getMarkerData(new MarkerDataService.MarkerDataCallback() {
                @Override
                public void onSuccess(MarkerResponse response) {
                    applyMarkersToKLineData(list, response);
                    latch.countDown();
                }

                @Override
                public void onError(String error) {
                    Log.e("KLineRepository", "获取标记数据失败: " + error);
                    // 即使获取标记数据失败，也继续返回K线数据（只是没有标记）
                    latch.countDown();
                }
            });

            try {
                // 等待标记数据处理完成
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

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
     * 生成基础K线数据（不包含标记）
     */
    private List<KLineEntry> generateBaseKLineData() {
        List<KLineEntry> list = new ArrayList<>();
        // 使用固定起始价格，确保有明显的趋势
        float base = 100f;

        // 生成日期序列 - 从3个月前开始，每天一条数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -100); // 从100天前开始

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
            list.add(entry);

            // 日期向前推进一天
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return list;
    }

    /**
     * 将外部标记数据应用到K线数据中
     *
     * @param kLineData      K线数据列表
     * @param markerResponse 外部标记数据
     */
    private void applyMarkersToKLineData(List<KLineEntry> kLineData, MarkerResponse markerResponse) {
        if (markerResponse == null || markerResponse.items == null || markerResponse.items.isEmpty()) {
            Log.d("KLineRepository", "没有标记数据需要应用");
            return;
        }

        // 创建日期到K线条目的映射，便于快速查找
        Map<String, KLineEntry> dateToEntryMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (KLineEntry entry : kLineData) {
            String dateStr = dateFormat.format(entry.date);
            dateToEntryMap.put(dateStr, entry);
        }

        // 输出前10个和后10个日期用于调试
        Log.d("KLineRepository", "可用的K线日期范围（前10个）:");
        for (int i = 0; i < Math.min(10, kLineData.size()); i++) {
            Log.d("KLineRepository", "  " + i + ": " + dateFormat.format(kLineData.get(i).date));
        }
        Log.d("KLineRepository", "可用的K线日期范围（后10个）:");
        for (int i = Math.max(0, kLineData.size() - 10); i < kLineData.size(); i++) {
            Log.d("KLineRepository", "  " + i + ": " + dateFormat.format(kLineData.get(i).date));
        }

        // 遍历标记数据，应用到对应日期的K线条目
        int appliedMarkers = 0;
        for (MarkerResponse.MarkerItem markerItem : markerResponse.items) {
            KLineEntry targetEntry = dateToEntryMap.get(markerItem.flag_at);

            if (targetEntry != null) {
                // 如果该位置已有标记，跳过（可根据需求调整策略）
                if (targetEntry.hasMarker) {
                    Log.w("KLineRepository", "日期 " + markerItem.flag_at + " 已有标记，跳过新标记");
                    continue;
                }

                // 应用标记
                targetEntry.hasMarker = true;
                targetEntry.markerType = KLineEntry.typeFromInt(markerItem.type);

                // 设置标记文本
                if (markerItem.extra != null && !markerItem.extra.trim().isEmpty()) {
                    targetEntry.markerText = markerItem.extra;
                    Log.d("KLineRepository", "使用extra文本: '" + markerItem.extra + "'");
                } else {
                    // 如果没有指定文本，使用默认文本
                    Log.d("KLineRepository", "extra为空，使用默认文本。extra='" + markerItem.extra + "'");
                    switch (markerItem.type) {
                        case 0:
                            targetEntry.markerText = "N"; // 数字标记默认文本
                            break;
                        case 1:
                            targetEntry.markerText = "B"; // 买入标记
                            break;
                        case 2:
                            targetEntry.markerText = "S"; // 卖出标记
                            break;
                        case 3:
                            targetEntry.markerText = "↑"; // 上三角标记
                            break;
                        case 4:
                            targetEntry.markerText = "↓"; // 下三角标记
                            break;
                        default:
                            targetEntry.markerText = "?";
                            break;
                    }
                }

                appliedMarkers++;
                Log.d("KLineRepository", "已应用标记到日期 " + markerItem.flag_at +
                        ", 类型: " + markerItem.type + ", 文本: " + targetEntry.markerText);
            } else {
                Log.w("KLineRepository", "未找到日期 " + markerItem.flag_at + " 对应的K线数据");
            }
        }

        Log.d("KLineRepository", "标记应用完成，共应用 " + appliedMarkers + " 个标记");
    }
} 