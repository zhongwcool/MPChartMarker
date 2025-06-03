package com.alex.klinemarker.data;

import android.graphics.Color;

/**
 * 标记颜色预设类
 * 提供统一的颜色方案，避免杂乱的颜色使用
 * 包含谷歌Logo四色、股票经典色彩以及自定义颜色支持
 */
public class MarkerColors {

    // ========== 谷歌Logo四色 ==========
    /**
     * 谷歌蓝 - 适用于信息、提示类标记
     */
    public static final int GOOGLE_BLUE = 0xFF4285F4;

    /**
     * 谷歌红 - 适用于警告、错误、卖出类标记
     */
    public static final int GOOGLE_RED = 0xFFEA4335;

    /**
     * 谷歌黄 - 适用于重要、关注类标记
     */
    public static final int GOOGLE_YELLOW = 0xFFFBBC04;

    /**
     * 谷歌绿 - 适用于成功、买入类标记
     */
    public static final int GOOGLE_GREEN = 0xFF34A853;

    // ========== 股票经典色彩 ==========
    /**
     * 股票红 - 中国股市下跌色（红色）
     */
    public static final int STOCK_RED = 0xFFFF4444;

    /**
     * 股票绿 - 中国股市上涨色（绿色）
     */
    public static final int STOCK_GREEN = 0xFF00AA00;

    // ========== 经典紫色 ==========
    /**
     * 菱形紫 - 经典的菱形标记紫色
     */
    public static final int DIAMOND_PURPLE = 0xFF9C27B0;

    // ========== 预设颜色组合 ==========
    /**
     * 主要颜色调色板 - 包含最常用的7种颜色
     */
    public static final int[] PRIMARY_PALETTE = {
            GOOGLE_BLUE,        // 蓝色 - 信息
            GOOGLE_RED,         // 红色 - 警告/卖出
            GOOGLE_GREEN,       // 绿色 - 成功/买入
            GOOGLE_YELLOW,      // 黄色 - 重要
            STOCK_RED,          // 股票红
            STOCK_GREEN,        // 股票绿
            DIAMOND_PURPLE      // 紫色
    };

    /**
     * 扩展颜色调色板 - 包含更多颜色选择
     */
    public static final int[] EXTENDED_PALETTE = {
            // 主要颜色
            GOOGLE_BLUE,
            GOOGLE_RED,
            GOOGLE_GREEN,
            GOOGLE_YELLOW,
            STOCK_RED,
            STOCK_GREEN,
            DIAMOND_PURPLE,

            // 深色变体
            0xFF1976D2,         // 深蓝
            0xFFD32F2F,         // 深红
            0xFF388E3C,         // 深绿
            0xFFF57C00,         // 深橙

            // 中性色
            0xFF616161,         // 深灰
            0xFF9E9E9E,         // 中灰
            0xFF000000,         // 黑色
            0xFFFFFFFF          // 白色
    };

    // ========== 颜色工具方法 ==========

    /**
     * 获取主要调色板中的颜色
     *
     * @param index 颜色索引（0-6）
     * @return 颜色值，超出范围时返回谷歌蓝
     */
    public static int getPrimaryColor(int index) {
        if (index >= 0 && index < PRIMARY_PALETTE.length) {
            return PRIMARY_PALETTE[index];
        }
        return GOOGLE_BLUE; // 默认返回谷歌蓝
    }

    /**
     * 获取扩展调色板中的颜色
     *
     * @param index 颜色索引
     * @return 颜色值，超出范围时返回谷歌蓝
     */
    public static int getExtendedColor(int index) {
        if (index >= 0 && index < EXTENDED_PALETTE.length) {
            return EXTENDED_PALETTE[index];
        }
        return GOOGLE_BLUE; // 默认返回谷歌蓝
    }

    /**
     * 根据标记类型获取推荐颜色
     *
     * @param markerType 标记类型
     * @return 推荐的颜色值
     */
    public static int getRecommendedColor(MarkerType markerType) {
        switch (markerType) {
            case BUY:
            case SUCCESS:
                return GOOGLE_GREEN;
            case SELL:
            case ERROR:
            case WARNING:
                return GOOGLE_RED;
            case INFO:
                return GOOGLE_BLUE;
            case IMPORTANT:
                return GOOGLE_YELLOW;
            case EVENT:
                return DIAMOND_PURPLE;
            case STOCK_UP:
                return STOCK_GREEN;
            case STOCK_DOWN:
                return STOCK_RED;
            default:
                return GOOGLE_BLUE;
        }
    }

    /**
     * 获取颜色的深色变体（用于提高对比度）
     *
     * @param color 原始颜色
     * @return 深色变体
     */
    public static int getDarkerVariant(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // 降低亮度到80%
        return Color.HSVToColor(hsv);
    }

    /**
     * 获取颜色的浅色变体（用于背景等）
     *
     * @param color 原始颜色
     * @return 浅色变体
     */
    public static int getLighterVariant(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1.0f, hsv[2] * 1.2f); // 提高亮度到120%，但不超过1.0
        return Color.HSVToColor(hsv);
    }

    /**
     * 获取颜色的半透明版本
     *
     * @param color 原始颜色
     * @param alpha 透明度（0-255）
     * @return 半透明颜色
     */
    public static int getTransparentVariant(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /**
     * 标记类型枚举 - 用于颜色推荐
     */
    public enum MarkerType {
        BUY,            // 买入
        SELL,           // 卖出
        INFO,           // 信息
        WARNING,        // 警告
        ERROR,          // 错误
        SUCCESS,        // 成功
        IMPORTANT,      // 重要
        EVENT,          // 事件
        STOCK_UP,       // 股票上涨
        STOCK_DOWN,     // 股票下跌
        CUSTOM          // 自定义
    }

    // ========== 预设颜色方案 ==========

    /**
     * 获取买入相关的颜色方案
     */
    public static class BuyColors {
        public static final int BACKGROUND = GOOGLE_GREEN;
        public static final int TEXT = 0xFFFFFFFF;
        public static final int LINE = getDarkerVariant(GOOGLE_GREEN);
    }

    /**
     * 获取卖出相关的颜色方案
     */
    public static class SellColors {
        public static final int BACKGROUND = GOOGLE_RED;
        public static final int TEXT = 0xFFFFFFFF;
        public static final int LINE = getDarkerVariant(GOOGLE_RED);
    }

    /**
     * 获取信息相关的颜色方案
     */
    public static class InfoColors {
        public static final int BACKGROUND = GOOGLE_BLUE;
        public static final int TEXT = 0xFFFFFFFF;
        public static final int LINE = getDarkerVariant(GOOGLE_BLUE);
    }

    /**
     * 获取重要标记的颜色方案
     */
    public static class ImportantColors {
        public static final int BACKGROUND = GOOGLE_YELLOW;
        public static final int TEXT = 0xFF000000; // 黄色背景用黑色文字
        public static final int LINE = getDarkerVariant(GOOGLE_YELLOW);
    }

    /**
     * 获取事件标记的颜色方案
     */
    public static class EventColors {
        public static final int BACKGROUND = DIAMOND_PURPLE;
        public static final int TEXT = 0xFFFFFFFF;
        public static final int LINE = getDarkerVariant(DIAMOND_PURPLE);
    }

    /**
     * 获取股票相关的颜色方案
     */
    public static class StockColors {
        public static final int UP_BACKGROUND = STOCK_GREEN;
        public static final int DOWN_BACKGROUND = STOCK_RED;
        public static final int TEXT = 0xFFFFFFFF;
        public static final int UP_LINE = getDarkerVariant(STOCK_GREEN);
        public static final int DOWN_LINE = getDarkerVariant(STOCK_RED);
    }
} 