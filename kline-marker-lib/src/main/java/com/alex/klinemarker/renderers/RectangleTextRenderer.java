package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 矩形背景 + 文字的标记渲染器
 * 用于渲染如BUY、SELL等标记
 */
public class RectangleTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;

    public RectangleTextRenderer(float density) {
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
        float markerSizePx = markerSize * density;
        float paddingPx = config.getPadding() * density;

        // 设置背景颜色
        int bgColor = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker, config);
        backgroundPaint.setColor(bgColor);

        // 设置文字颜色和大小
        int textColor = marker.getTextColor() != 0 ? marker.getTextColor() : config.getTextColor();
        textPaint.setColor(textColor);
        textPaint.setTextSize(config.getTextSize() * density);
        textPaint.setTypeface(config.getTextTypeface());

        // 计算矩形边界
        String text = marker.getText();
        float textWidth = textPaint.measureText(text);
        float rectWidth = Math.max(markerSizePx, textWidth + paddingPx * 2);
        float rectHeight = markerSizePx;

        float left = centerX - rectWidth / 2;
        float top = centerY - rectHeight / 2;
        float right = centerX + rectWidth / 2;
        float bottom = centerY + rectHeight / 2;

        // 绘制圆角矩形背景
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rectF, paddingPx, paddingPx, backgroundPaint);

        // 绘制文字
        float textY = centerY + (textPaint.getTextSize() / 3);
        canvas.drawText(text, centerX, textY, textPaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float markerSizePx = markerSize * density;
        float paddingPx = config.getPadding() * density;

        textPaint.setTextSize(config.getTextSize() * density);
        float textWidth = textPaint.measureText(marker.getText());

        return Math.max(markerSizePx, textWidth + paddingPx * 2);
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density;
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.RECTANGLE_TEXT;
    }

    private int getDefaultColor(MarkerData marker, MarkerConfig config) {
        switch (marker.getType()) {
            case BUY:
                return config.getBuyColor();
            case SELL:
                return config.getSellColor();
            default:
                return config.getBuyColor();
        }
    }
} 