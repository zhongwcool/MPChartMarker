package com.alex.mpchart.marker.data.repository;

import android.util.Log;

import com.alex.mpchart.marker.data.model.MarkerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 标记数据服务类，用于获取外部指定的标记数据
 */
public class MarkerDataService {
    private static final String TAG = "MarkerDataService";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 获取标记数据（模拟API调用）
     *
     * @param callback 回调接口
     */
    public void getMarkerData(MarkerDataCallback callback) {
        executor.execute(() -> {
            try {
                // 模拟网络延迟
                Thread.sleep(500);

                // 模拟返回的标记数据
                MarkerResponse response = createMockMarkerData();

                callback.onSuccess(response);
                Log.d(TAG, "标记数据获取成功，共 " + response.items.size() + " 个标记");

                // 输出所有标记数据用于调试
                for (MarkerResponse.MarkerItem item : response.items) {
                    Log.d(TAG, "标记: 日期=" + item.flag_at + ", 类型=" + item.type + ", 文本=" + item.extra);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                callback.onError("获取标记数据时发生错误: " + e.getMessage());
            } catch (Exception e) {
                callback.onError("获取标记数据失败: " + e.getMessage());
            }
        });
    }

    /**
     * 创建模拟的标记数据
     */
    private MarkerResponse createMockMarkerData() {
        MarkerResponse response = new MarkerResponse();
        response.items = new ArrayList<>();
        response.total = 8;
        response.page = 1;
        response.size = 8;
        response.pages = 1;

        // 添加一些示例标记数据
        List<MarkerResponse.MarkerItem> items = response.items;

        // 计算相对日期（与K线数据生成逻辑保持一致）
        // K线数据从100天前开始，所以我们也从那个日期开始计算
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -100); // 从100天前开始
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

        // 为了确保日期匹配，我们使用偏移量来生成具体的标记日期
        java.util.Calendar markerCalendar;

        // 数字标记 - 第5天、第15天、第25天
        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 5);
        items.add(new MarkerResponse.MarkerItem("2024-12-15T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 0, "1"));

        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 15);
        items.add(new MarkerResponse.MarkerItem("2024-12-25T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 0, "2"));

        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 25);
        items.add(new MarkerResponse.MarkerItem("2025-01-05T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 0, "虎虎虎"));

        // 买入卖出标记 - 第20天、第35天
        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 20);
        items.add(new MarkerResponse.MarkerItem("2024-12-30T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 1, "B"));

        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 35);
        items.add(new MarkerResponse.MarkerItem("2025-01-10T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 2, "S"));

        // 上下三角标记 - 第45天、第55天、第65天
        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 45);
        items.add(new MarkerResponse.MarkerItem("2025-01-20T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 3, "激增"));

        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 55);
        items.add(new MarkerResponse.MarkerItem("2025-01-30T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 4, "陡降"));

        markerCalendar = (java.util.Calendar) calendar.clone();
        markerCalendar.add(java.util.Calendar.DAY_OF_YEAR, 65);
        items.add(new MarkerResponse.MarkerItem("2025-02-10T07:36:19.755Z", dateFormat.format(markerCalendar.getTime()), 0, "特殊标记"));

        return response;
    }

    /**
     * 标记数据回调接口
     */
    public interface MarkerDataCallback {
        void onSuccess(MarkerResponse response);

        void onError(String error);
    }
} 