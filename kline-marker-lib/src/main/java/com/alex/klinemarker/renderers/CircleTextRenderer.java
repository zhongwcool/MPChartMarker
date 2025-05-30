package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 圆形背景 + 文字的标记渲染器
 * 用于渲染事件、信息等标记
 */
public class CircleTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;

    public CircleTextRenderer(float density) {
        this.density = density;

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float radius = markerSize * density / 2;

        // 设置背景颜色
        int bgColor = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker, config);
        backgroundPaint.setColor(bgColor);

        // 设置文字颜色和大小
        int textColor = marker.getTextColor() != 0 ? marker.getTextColor() : config.getTextColor();
        textPaint.setColor(textColor);
        textPaint.setTextSize(config.getTextSize() * density * 0.8f); // 圆形中的文字稍小一些
        textPaint.setTypeface(config.getTextTypeface());

        // 如果有文字，可能需要调整圆的大小
        String text = marker.getText();
        if (text != null && !text.isEmpty()) {
            float textWidth = textPaint.measureText(text);
            float minRadius = (textWidth + config.getPadding() * density) / 2;
            radius = Math.max(radius, minRadius);
        }

        // 绘制圆形背景
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // 绘制文字
        if (text != null && !text.isEmpty()) {
            float textY = centerY + (textPaint.getTextSize() / 3);
            canvas.drawText(text, centerX, textY, textPaint);
        }
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float diameter = markerSize * density;

        String text = marker.getText();
        if (text != null && !text.isEmpty()) {
            textPaint.setTextSize(config.getTextSize() * density * 0.8f);
            float textWidth = textPaint.measureText(text);
            float minDiameter = textWidth + config.getPadding() * density;
            diameter = Math.max(diameter, minDiameter);
        }

        return diameter;
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        return getMarkerWidth(marker, config); // 圆形宽高相等
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.CIRCLE_TEXT;
    }

    private int getDefaultColor(MarkerData marker, MarkerConfig config) {
        switch (marker.getType()) {
            case EVENT:
                return config.getNumberColor(); // 使用数字标记的颜色作为事件颜色
            case INFO:
                return 0xFF2196F3; // 蓝色
            case WARNING:
                return 0xFFFF9800; // 橙色
            default:
                return config.getBuyColor();
        }
    }
} 