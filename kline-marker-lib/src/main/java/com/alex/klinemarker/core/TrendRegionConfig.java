package com.alex.klinemarker.core;

import android.graphics.Color;

/**
 * 趋势区间配置类
 * 用于自定义趋势区间的外观和行为
 */
public class TrendRegionConfig {

    // 默认颜色配置
    public static final int DEFAULT_RISING_COLOR = Color.parseColor("#4CAF50");    // 绿色
    public static final int DEFAULT_FALLING_COLOR = Color.parseColor("#F44336");   // 红色
    public static final int DEFAULT_NEUTRAL_COLOR = Color.parseColor("#2196F3");   // 蓝色

    // 透明度配置
    private float topAlpha = 0.30f;      // 顶部透明度
    private float bottomAlpha = 0.10f;   // 底部透明度

    // 颜色配置
    private int risingColor = DEFAULT_RISING_COLOR;
    private int fallingColor = DEFAULT_FALLING_COLOR;
    private int neutralColor = DEFAULT_NEUTRAL_COLOR;

    // 偏移配置
    private float offsetDp = 8f;         // 偏移距离（dp）

    // 平滑配置
    private int smoothWindowSize = 3;    // 平滑窗口大小
    private boolean enableSmoothing = true; // 是否启用平滑

    // 渐变配置
    private boolean enableGradient = true;  // 是否启用渐变
    private boolean enableBezierCurve = true; // 是否启用贝塞尔曲线

    // 性能优化配置
    private boolean enablePerformanceMode = false; // 性能模式，禁用复杂渲染
    private int maxVisibleRegions = 10; // 最大可见区间数量

    public TrendRegionConfig() {
        // 使用默认配置
    }

    // Builder模式
    public static class Builder {
        private final TrendRegionConfig config = new TrendRegionConfig();

        public Builder topAlpha(float alpha) {
            config.topAlpha = alpha;
            return this;
        }

        public Builder bottomAlpha(float alpha) {
            config.bottomAlpha = alpha;
            return this;
        }

        public Builder risingColor(int color) {
            config.risingColor = color;
            return this;
        }

        public Builder fallingColor(int color) {
            config.fallingColor = color;
            return this;
        }

        public Builder neutralColor(int color) {
            config.neutralColor = color;
            return this;
        }

        public Builder offsetDp(float offset) {
            config.offsetDp = offset;
            return this;
        }

        public Builder smoothWindowSize(int size) {
            config.smoothWindowSize = size;
            return this;
        }

        public Builder enableSmoothing(boolean enable) {
            config.enableSmoothing = enable;
            return this;
        }

        public Builder enableGradient(boolean enable) {
            config.enableGradient = enable;
            return this;
        }

        public Builder enableBezierCurve(boolean enable) {
            config.enableBezierCurve = enable;
            return this;
        }

        public Builder enablePerformanceMode(boolean enable) {
            config.enablePerformanceMode = enable;
            if (enable) {
                // 性能模式：禁用复杂渲染
                config.enableGradient = false;
                config.enableBezierCurve = false;
                config.enableSmoothing = false;
                config.smoothWindowSize = 1;
            }
            return this;
        }

        public Builder maxVisibleRegions(int max) {
            config.maxVisibleRegions = max;
            return this;
        }

        public TrendRegionConfig build() {
            return config;
        }
    }

    // Getters
    public float getTopAlpha() {
        return topAlpha;
    }

    public float getBottomAlpha() {
        return bottomAlpha;
    }

    public int getRisingColor() {
        return risingColor;
    }

    public int getFallingColor() {
        return fallingColor;
    }

    public int getNeutralColor() {
        return neutralColor;
    }

    public float getOffsetDp() {
        return offsetDp;
    }

    public int getSmoothWindowSize() {
        return smoothWindowSize;
    }

    public boolean isEnableSmoothing() {
        return enableSmoothing;
    }

    public boolean isEnableGradient() {
        return enableGradient;
    }

    public boolean isEnableBezierCurve() {
        return enableBezierCurve;
    }

    public boolean isEnablePerformanceMode() {
        return enablePerformanceMode;
    }

    public int getMaxVisibleRegions() {
        return maxVisibleRegions;
    }
} 