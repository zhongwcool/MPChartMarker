package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 纯文字标记渲染器
 * 只显示文字，无背景形状
 * 支持多个汉字显示，文字从指示线末端开始，使用加粗字体
 */
public class TextOnlyRenderer implements IMarkerRenderer {

    private final Paint textPaint;
    private final float density;
    private final Rect textBounds = new Rect();

    public TextOnlyRenderer(float density) {
        this.density = density;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
        // 优化文字渲染质量，特别适合汉字显示
        textPaint.setSubpixelText(true);
        textPaint.setLinearText(true);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        String text = marker.getText();
        if (text == null || text.isEmpty() || !marker.getConfig().isShowText()) {
            return;
        }

        // 设置文字样式
        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.setColor(marker.getConfig().getTextColor());
        textPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 测量文字尺寸以正确定位
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        // 计算文字基线位置，确保汉字正确居中
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textY = centerY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;

        // 添加小的偏移，让文字稍微远离指示线末端
        float textX = centerX + 4 * density;

        // 绘制文字（从指示线末端开始，支持任意长度的文字，包括多个汉字）
        canvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker) {
        if (!marker.getConfig().isShowText() || marker.getText() == null || marker.getText().isEmpty()) {
            return 4 * density; // 只有偏移距离
        }

        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        float textWidth = textPaint.measureText(marker.getText());
        return textWidth + 4 * density; // 文字宽度 + 偏移距离
    }

    @Override
    public float getMarkerHeight(MarkerData marker) {
        if (!marker.getConfig().isShowText() || marker.getText() == null || marker.getText().isEmpty()) {
            return 0;
        }

        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.getTextBounds(marker.getText(), 0, marker.getText().length(), textBounds);
        return textBounds.height();
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.NONE;
    }
} 