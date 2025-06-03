package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 矩形背景 + 文字标记渲染器
 */
public class RectangleTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;
    private final Rect textBounds = new Rect();

    public RectangleTextRenderer(float density) {
        this.density = density;

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        String text = marker.getText();
        if (text == null || text.isEmpty()) {
            text = "";
        }

        // 设置文字样式
        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.setColor(marker.getConfig().getTextColor());

        // 测量文字尺寸
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        float textWidth = textBounds.width();
        float textHeight = textBounds.height();

        // 计算矩形大小 - 修复矩形为正方形
        float padding = marker.getConfig().getMarkerSize() * density * 0.3f;
        float minSize = marker.getConfig().getMarkerSize() * density; // 统一最小尺寸

        float rectWidth = Math.max(minSize, textWidth + padding * 2);
        float rectHeight = Math.max(minSize, textHeight + padding); // 改为统一的minSize，确保是正方形

        // 计算矩形位置
        RectF rect = new RectF(
                centerX - rectWidth / 2,
                centerY - rectHeight / 2,
                centerX + rectWidth / 2,
                centerY + rectHeight / 2
        );

        // 绘制矩形背景
        backgroundPaint.setColor(marker.getConfig().getBackgroundColor());
        backgroundPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));
        canvas.drawRoundRect(rect, 4 * density, 4 * density, backgroundPaint);

        // 绘制文字
        if (marker.getConfig().isShowText() && !text.isEmpty()) {
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
            return marker.getConfig().getMarkerSize() * density; // 改为统一尺寸，确保正方形
        }

        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.getTextBounds(marker.getText(), 0, marker.getText().length(), textBounds);

        float padding = marker.getConfig().getMarkerSize() * density * 0.3f;
        float minSize = marker.getConfig().getMarkerSize() * density; // 改为统一的最小尺寸
        float contentHeight = textBounds.height() + padding;

        return Math.max(minSize, contentHeight); // 使用统一的最小尺寸
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.RECTANGLE;
    }
} 