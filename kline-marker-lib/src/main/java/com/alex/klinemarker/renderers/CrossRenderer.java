package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 十字标记渲染器
 * 绘制十字形标记
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
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 获取标记大小
        float size = marker.getConfig().getMarkerSize() * density / 3;

        // 设置线条样式
        crossPaint.setColor(marker.getConfig().getBackgroundColor());
        crossPaint.setStrokeWidth(marker.getConfig().getLineWidth() * density);
        crossPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 绘制十字
        // 水平线
        canvas.drawLine(centerX - size, centerY, centerX + size, centerY, crossPaint);
        // 垂直线
        canvas.drawLine(centerX, centerY - size, centerX, centerY + size, crossPaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker) {
        return marker.getConfig().getMarkerSize() * density;
    }

    @Override
    public float getMarkerHeight(MarkerData marker) {
        return marker.getConfig().getMarkerSize() * density;
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.CROSS;
    }
} 