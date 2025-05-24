package com.alex.mpchart.marker.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 趋势区间数据模型
 * 基于起始和结束日期定义的趋势区间
 */
public class TrendRegion {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public String start;        // 起始日期，格式：yyyy-MM-dd
    public String end;          // 结束日期，格式：yyyy-MM-dd，可以为null表示到最后
    public int size;            // 趋势强度或大小
    public String updatedAt;    // 更新时间

    public TrendRegion(String start, String end, int size, String updatedAt) {
        this.start = start;
        this.end = end;
        this.size = size;
        this.updatedAt = updatedAt;
    }

    /**
     * 获取起始日期的Date对象
     */
    public Date getStartDate() {
        try {
            return DATE_FORMAT.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取结束日期的Date对象
     */
    public Date getEndDate() {
        if (end == null) {
            return null; // 表示到最后
        }
        try {
            return DATE_FORMAT.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查指定日期是否在该趋势区间内
     */
    public boolean containsDate(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            return containsDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查指定日期是否在该趋势区间内
     */
    public boolean containsDate(Date date) {
        if (date == null) return false;

        Date startDate = getStartDate();
        if (startDate == null) return false;

        // 检查起始日期
        if (date.before(startDate)) {
            return false;
        }

        // 检查结束日期
        Date endDate = getEndDate();
        return endDate == null || !date.after(endDate);
    }

    @Override
    public String toString() {
        return "TrendRegion{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", size=" + size +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
} 