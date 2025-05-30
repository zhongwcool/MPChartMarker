package com.alex.klinemarker.data;

import android.graphics.drawable.Drawable;

/**
 * 标记配置类
 * 定义标记的所有视觉属性
 */
public class MarkerConfig {

    // 基本属性
    private MarkerShape shape;           // 形状
    private MarkerPosition position;     // 位置（上/下）
    private boolean showText;            // 是否显示文字
    private boolean showLine;            // 是否显示引线

    // 颜色属性
    private int backgroundColor;         // 背景颜色
    private int textColor;              // 文字颜色
    private int lineColor;              // 引线颜色

    // 尺寸属性
    private float markerSize;           // 标记大小（dp）
    private float textSize;             // 文字大小（sp）
    private float lineWidth;            // 引线宽度（dp）

    // 引线属性
    private LineLength lineLength;      // 引线长度
    private boolean isDashedLine;       // 是否为虚线
    private float[] dashPattern;        // 虚线样式 [线段长度, 间隔长度]

    // 自定义图标
    private Drawable customIcon;        // 自定义图标（当shape为CUSTOM_ICON时使用）

    // 其他属性
    private float alpha;                // 透明度 (0-1)
    private int zIndex;                 // 绘制层级（数值越大越靠前）

    /**
     * 默认构造函数
     */
    public MarkerConfig() {
        this.shape = MarkerShape.CIRCLE;
        this.position = MarkerPosition.AUTO;
        this.showText = true;
        this.showLine = true;
        this.backgroundColor = 0xFF2196F3; // 默认蓝色
        this.textColor = 0xFFFFFFFF;       // 默认白色
        this.lineColor = 0xFF2196F3;       // 默认蓝色
        this.markerSize = 24f;             // 默认24dp
        this.textSize = 12f;               // 默认12sp
        this.lineWidth = 1f;               // 默认1dp
        this.lineLength = LineLength.MEDIUM;
        this.isDashedLine = true;          // 默认虚线
        this.dashPattern = new float[]{5f, 3f}; // 默认虚线样式
        this.alpha = 1f;
        this.zIndex = 0;
    }

    /**
     * 构建器模式 - 用于链式调用
     */
    public static class Builder {
        private final MarkerConfig config;

        public Builder() {
            config = new MarkerConfig();
        }

        public Builder shape(MarkerShape shape) {
            config.shape = shape;
            return this;
        }

        public Builder position(MarkerPosition position) {
            config.position = position;
            return this;
        }

        public Builder showText(boolean show) {
            config.showText = show;
            return this;
        }

        public Builder showLine(boolean show) {
            config.showLine = show;
            return this;
        }

        public Builder backgroundColor(int color) {
            config.backgroundColor = color;
            return this;
        }

        public Builder textColor(int color) {
            config.textColor = color;
            return this;
        }

        public Builder lineColor(int color) {
            config.lineColor = color;
            return this;
        }

        public Builder markerSize(float size) {
            config.markerSize = size;
            return this;
        }

        public Builder textSize(float size) {
            config.textSize = size;
            return this;
        }

        public Builder lineWidth(float width) {
            config.lineWidth = width;
            return this;
        }

        public Builder lineLength(LineLength length) {
            config.lineLength = length;
            return this;
        }

        public Builder dashedLine(boolean dashed) {
            config.isDashedLine = dashed;
            return this;
        }

        public Builder dashPattern(float[] pattern) {
            config.dashPattern = pattern;
            return this;
        }

        public Builder customIcon(Drawable icon) {
            config.customIcon = icon;
            config.shape = MarkerShape.CUSTOM_ICON;
            return this;
        }

        public Builder alpha(float alpha) {
            config.alpha = alpha;
            return this;
        }

        public Builder zIndex(int zIndex) {
            config.zIndex = zIndex;
            return this;
        }

        public MarkerConfig build() {
            return config;
        }
    }

    /**
     * 复制当前配置
     */
    public MarkerConfig copy() {
        MarkerConfig copy = new MarkerConfig();
        copy.shape = this.shape;
        copy.position = this.position;
        copy.showText = this.showText;
        copy.showLine = this.showLine;
        copy.backgroundColor = this.backgroundColor;
        copy.textColor = this.textColor;
        copy.lineColor = this.lineColor;
        copy.markerSize = this.markerSize;
        copy.textSize = this.textSize;
        copy.lineWidth = this.lineWidth;
        copy.lineLength = this.lineLength;
        copy.isDashedLine = this.isDashedLine;
        copy.dashPattern = this.dashPattern != null ? this.dashPattern.clone() : null;
        copy.customIcon = this.customIcon;
        copy.alpha = this.alpha;
        copy.zIndex = this.zIndex;
        return copy;
    }

    // Getters and Setters
    public MarkerShape getShape() {
        return shape;
    }

    public void setShape(MarkerShape shape) {
        this.shape = shape;
    }

    public MarkerPosition getPosition() {
        return position;
    }

    public void setPosition(MarkerPosition position) {
        this.position = position;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(float markerSize) {
        this.markerSize = markerSize;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public LineLength getLineLength() {
        return lineLength;
    }

    public void setLineLength(LineLength lineLength) {
        this.lineLength = lineLength;
    }

    public boolean isDashedLine() {
        return isDashedLine;
    }

    public void setDashedLine(boolean dashedLine) {
        isDashedLine = dashedLine;
    }

    public float[] getDashPattern() {
        return dashPattern;
    }

    public void setDashPattern(float[] dashPattern) {
        this.dashPattern = dashPattern;
    }

    public Drawable getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(Drawable customIcon) {
        this.customIcon = customIcon;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
} 