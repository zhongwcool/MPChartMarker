package com.alex.klinemarker.core;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * 标记配置类
 * 用于自定义标记的外观和行为
 */
public class MarkerConfig {

    // 默认颜色配置
    public static final int DEFAULT_BUY_COLOR = Color.parseColor("#4CAF50");      // 绿色
    public static final int DEFAULT_SELL_COLOR = Color.parseColor("#F44336");     // 红色
    public static final int DEFAULT_NUMBER_COLOR = Color.parseColor("#80BDBDBD"); // 半透明浅灰色
    public static final int DEFAULT_UP_TRIANGLE_COLOR = Color.parseColor("#FF5722"); // 橙红色
    public static final int DEFAULT_DOWN_TRIANGLE_COLOR = Color.parseColor("#9C27B0"); // 紫色

    // 标记大小配置
    private float markerSize = 12f;          // 标记大小（dp）
    private float textSize = 9f;             // 文字大小（sp）
    private float padding = 3f;              // 内边距（dp）
    private float lineWidth = 1f;            // 线条宽度（dp）

    // 虚线配置
    private float fixedLineLength = 30f;     // 固定虚线长度（dp）
    private float shortLineLength = 5f;      // 短虚线长度（dp）
    private float dashLength = 3f;           // 虚线段长度（dp）
    private float dashGap = 3f;              // 虚线间隔（dp）

    // 颜色配置
    private int buyColor = DEFAULT_BUY_COLOR;
    private int sellColor = DEFAULT_SELL_COLOR;
    private int numberColor = DEFAULT_NUMBER_COLOR;
    private int upTriangleColor = DEFAULT_UP_TRIANGLE_COLOR;
    private int downTriangleColor = DEFAULT_DOWN_TRIANGLE_COLOR;
    private int textColor = Color.WHITE;
    private int numberTextColor = Color.parseColor("#333333");

    // 字体配置
    private Typeface textTypeface = Typeface.DEFAULT_BOLD;

    // 位置配置
    private float markerOffsetMultiplier = 2.0f;  // 标记偏移倍数
    private float triangleOffsetMultiplier = 1.5f; // 三角形标记偏移倍数

    public MarkerConfig() {
        // 使用默认配置
    }

    // Builder模式
    public static class Builder {
        private MarkerConfig config = new MarkerConfig();

        public Builder markerSize(float size) {
            config.markerSize = size;
            return this;
        }

        public Builder textSize(float size) {
            config.textSize = size;
            return this;
        }

        public Builder padding(float padding) {
            config.padding = padding;
            return this;
        }

        public Builder lineWidth(float width) {
            config.lineWidth = width;
            return this;
        }

        public Builder fixedLineLength(float length) {
            config.fixedLineLength = length;
            return this;
        }

        public Builder shortLineLength(float length) {
            config.shortLineLength = length;
            return this;
        }

        public Builder dashLength(float length) {
            config.dashLength = length;
            return this;
        }

        public Builder dashGap(float gap) {
            config.dashGap = gap;
            return this;
        }

        public Builder buyColor(int color) {
            config.buyColor = color;
            return this;
        }

        public Builder sellColor(int color) {
            config.sellColor = color;
            return this;
        }

        public Builder numberColor(int color) {
            config.numberColor = color;
            return this;
        }

        public Builder upTriangleColor(int color) {
            config.upTriangleColor = color;
            return this;
        }

        public Builder downTriangleColor(int color) {
            config.downTriangleColor = color;
            return this;
        }

        public Builder textColor(int color) {
            config.textColor = color;
            return this;
        }

        public Builder numberTextColor(int color) {
            config.numberTextColor = color;
            return this;
        }

        public Builder textTypeface(Typeface typeface) {
            config.textTypeface = typeface;
            return this;
        }

        public Builder markerOffsetMultiplier(float multiplier) {
            config.markerOffsetMultiplier = multiplier;
            return this;
        }

        public Builder triangleOffsetMultiplier(float multiplier) {
            config.triangleOffsetMultiplier = multiplier;
            return this;
        }

        public MarkerConfig build() {
            return config;
        }
    }

    // Getters
    public float getMarkerSize() {
        return markerSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public float getPadding() {
        return padding;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public float getFixedLineLength() {
        return fixedLineLength;
    }

    public float getShortLineLength() {
        return shortLineLength;
    }

    public float getDashLength() {
        return dashLength;
    }

    public float getDashGap() {
        return dashGap;
    }

    public int getBuyColor() {
        return buyColor;
    }

    public int getSellColor() {
        return sellColor;
    }

    public int getNumberColor() {
        return numberColor;
    }

    public int getUpTriangleColor() {
        return upTriangleColor;
    }

    public int getDownTriangleColor() {
        return downTriangleColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getNumberTextColor() {
        return numberTextColor;
    }

    public Typeface getTextTypeface() {
        return textTypeface;
    }

    public float getMarkerOffsetMultiplier() {
        return markerOffsetMultiplier;
    }

    public float getTriangleOffsetMultiplier() {
        return triangleOffsetMultiplier;
    }
} 