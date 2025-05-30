package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 圆形背景 + 文字标记渲染器
 */
public class CircleTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;
    private final Rect textBounds = new Rect();

    public CircleTextRenderer(float density) {
        this.density = density;

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        String text = marker.getText();
        if (text == null || text.isEmpty()) {
            text = ""; // 空文本也绘制背景
        }

        // 设置文字样式
        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.setColor(marker.getConfig().getTextColor());

        // 测量文字尺寸
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        float textWidth = textBounds.width();
        float textHeight = textBounds.height();

        // 计算圆形背景大小
        float padding = marker.getConfig().getMarkerSize() * density * 0.3f;
        float minRadius = marker.getConfig().getMarkerSize() * density / 2;
        float contentRadius = Math.max(textWidth / 2 + padding, textHeight / 2 + padding);
        float radius = Math.max(minRadius, contentRadius);

        // 绘制圆形背景
        backgroundPaint.setColor(marker.getConfig().getBackgroundColor());
        backgroundPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // 绘制文字
        if (marker.getConfig().isShowText() && !text.isEmpty()) {
            // 计算文字基线位置
            float textY = centerY + textHeight / 2f;
            canvas.drawText(text, centerX, textY, textPaint);
        }
    }

    @Override
    public float getMarkerWidth(MarkerData marker) {
        if (!marker.getConfig().isShowText() || marker.getText() == null || marker.getText().isEmpty()) {
            return marker.getConfig().getMarkerSize() * density;
        }

        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.getTextBounds(marker.getText(), 0, marker.getText().length(), textBounds);

        float padding = marker.getConfig().getMarkerSize() * density * 0.3f;
        float minWidth = marker.getConfig().getMarkerSize() * density;
        float contentWidth = textBounds.width() + padding * 2;

        return Math.max(minWidth, contentWidth);
    }

    @Override
    public float getMarkerHeight(MarkerData marker) {
        if (!marker.getConfig().isShowText() || marker.getText() == null || marker.getText().isEmpty()) {
            return marker.getConfig().getMarkerSize() * density;
        }

        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.getTextBounds(marker.getText(), 0, marker.getText().length(), textBounds);

        float padding = marker.getConfig().getMarkerSize() * density * 0.3f;
        float minHeight = marker.getConfig().getMarkerSize() * density;
        float contentHeight = textBounds.height() + padding * 2;

        return Math.max(minHeight, contentHeight);
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.CIRCLE;
    }
} 