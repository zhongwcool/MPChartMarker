package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 十字标记渲染器
 */
public class CrossRenderer implements IMarkerRenderer {

    private final Paint crossPaint;
    private final float density;

    public CrossRenderer(float density) {
        this.density = density;

        crossPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        crossPaint.setStyle(Paint.Style.STROKE);
        crossPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标准标记大小，十字标记使用较小的尺寸
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float halfSize = markerSize * density * 0.4f; // 调整为40%大小，避免过大

        // 设置颜色和线宽
        int color = marker.getColor() != 0 ? marker.getColor() : 0xFF000000; // 默认黑色
        crossPaint.setColor(color);
        crossPaint.setStrokeWidth(2.0f * density); // 稍微减小线宽

        // 绘制十字：水平线 + 垂直线
        canvas.drawLine(centerX - halfSize, centerY, centerX + halfSize, centerY, crossPaint); // 水平线
        canvas.drawLine(centerX, centerY - halfSize, centerX, centerY + halfSize, crossPaint); // 垂直线
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 统一大小
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 统一大小
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.CROSS;
    }
} 