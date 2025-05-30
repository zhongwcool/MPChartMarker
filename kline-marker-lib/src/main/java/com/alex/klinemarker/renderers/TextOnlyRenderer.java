package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 纯文字标记渲染器
 * 只绘制文字，连接线由KLineMarkerRenderer处理
 */
public class TextOnlyRenderer implements IMarkerRenderer {

    private final Paint textPaint;
    private final float density;

    public TextOnlyRenderer(float density) {
        this.density = density;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 设置文字颜色和大小
        int textColor = marker.getTextColor() != 0 ? marker.getTextColor() : config.getNumberTextColor();
        textPaint.setColor(textColor);
        textPaint.setTextSize(config.getTextSize() * density);
        textPaint.setTypeface(config.getTextTypeface());

        String text = marker.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        // 文字从传入的centerX位置开始绘制，与引出线末端对齐
        // 不添加额外的padding，因为centerX已经是引出线的末端位置
        float textX = centerX + 2 * density; // 只加一个很小的间距，避免文字与引出线重叠
        float textY = centerY + (textPaint.getTextSize() / 3);
        canvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        String text = marker.getText();
        if (text != null && !text.isEmpty()) {
            textPaint.setTextSize(config.getTextSize() * density);
            float textWidth = textPaint.measureText(text);
            float padding = config.getPadding() * density;
            return textWidth + padding * 2; // 文字宽度 + 左右边距
        }
        return 0;
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        return config.getTextSize() * density * 1.5f; // 文字高度
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.TEXT_ONLY;
    }
} 