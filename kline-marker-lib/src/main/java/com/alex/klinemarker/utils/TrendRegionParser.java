package com.alex.klinemarker.utils;

import android.util.Log;

import com.alex.klinemarker.data.TrendRegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 趋势区间JSON数据解析工具
 */
public class TrendRegionParser {
    private static final String TAG = "TrendRegionParser";

    /**
     * 解析JSON格式的趋势区间数据
     *
     * @param jsonData JSON字符串，格式如下：
     *                 {
     *                 "items": [
     *                 {
     *                 "updated_at": "2025-05-22T07:09:33.289230Z",
     *                 "start": "2025-05-15",
     *                 "end": null,
     *                 "size": 2,
     *                 "type": "RISING"
     *                 },
     *                 {
     *                 "updated_at": "2025-05-22T06:46:10.313136Z",
     *                 "start": "2025-03-19",
     *                 "end": "2025-03-24",
     *                 "size": 3,
     *                 "type": "FALLING"
     *                 }
     *                 ]
     *                 }
     * @return 解析后的趋势区间列表
     */
    public static List<TrendRegion> parseFromJson(String jsonData) {
        List<TrendRegion> regions = new ArrayList<>();

        if (jsonData == null || jsonData.trim().isEmpty()) {
            Log.w(TAG, "JSON data is null or empty");
            return regions;
        }

        try {
            JSONObject root = new JSONObject(jsonData);
            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String start = item.getString("start");
                String end = item.optString("end", null);
                if ("null".equals(end) || end.isEmpty()) {
                    end = null;
                }

                int size = item.getInt("size");
                String updatedAt = item.getString("updated_at");

                // 解析趋势类型
                String typeStr = item.optString("type", "NEUTRAL");
                TrendRegion.TrendType type = parseTrendType(typeStr);

                TrendRegion region = new TrendRegion(start, end, size, updatedAt, type);
                regions.add(region);

                Log.d(TAG, "Parsed trend region: " + region);
            }

            Log.i(TAG, "Successfully parsed " + regions.size() + " trend regions from JSON");

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON data: " + e.getMessage(), e);
            Log.e(TAG, "JSON data was: " + jsonData);
        }

        return regions;
    }

    /**
     * 解析趋势类型字符串
     */
    private static TrendRegion.TrendType parseTrendType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return TrendRegion.TrendType.NEUTRAL;
        }

        try {
            return TrendRegion.TrendType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Unknown trend type: " + typeStr + ", using NEUTRAL");
            return TrendRegion.TrendType.NEUTRAL;
        }
    }

    /**
     * 创建示例JSON数据用于测试
     *
     * @return 示例JSON字符串
     */
    public static String createSampleJson() {
        return "{\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T07:09:33.289230Z\",\n" +
                "      \"start\": \"2025-05-15\",\n" +
                "      \"end\": null,\n" +
                "      \"size\": 2,\n" +
                "      \"type\": \"RISING\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"updated_at\": \"2025-05-22T06:46:10.313136Z\",\n" +
                "      \"start\": \"2025-03-19\",\n" +
                "      \"end\": \"2025-03-24\",\n" +
                "      \"size\": 3,\n" +
                "      \"type\": \"FALLING\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
} 