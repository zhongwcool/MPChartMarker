package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 三角形标记渲染器
 * 用于渲染上三角和下三角标记
 */
public class TriangleRenderer implements IMarkerRenderer {

    private final Paint trianglePaint;
    private final float density;

    public TriangleRenderer(float density) {
        this.density = density;

        trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float halfSize = markerSize * density / 2;

        // 设置颜色
        int color = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker, config);
        trianglePaint.setColor(color);

        // 创建三角形路径
        Path trianglePath = new Path();

        if (marker.getStyle() == MarkerStyle.TRIANGLE_UP) {
            // 上三角
            trianglePath.moveTo(centerX, centerY - halfSize);
            trianglePath.lineTo(centerX - halfSize, centerY + halfSize);
            trianglePath.lineTo(centerX + halfSize, centerY + halfSize);
            trianglePath.close();
        } else {
            // 下三角
            trianglePath.moveTo(centerX, centerY + halfSize);
            trianglePath.lineTo(centerX - halfSize, centerY - halfSize);
            trianglePath.lineTo(centerX + halfSize, centerY - halfSize);
            trianglePath.close();
        }

        // 绘制三角形
        canvas.drawPath(trianglePath, trianglePaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density;
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density;
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.TRIANGLE_UP || style == MarkerStyle.TRIANGLE_DOWN;
    }

    private int getDefaultColor(MarkerData marker, MarkerConfig config) {
        if (marker.getStyle() == MarkerStyle.TRIANGLE_UP) {
            return config.getUpTriangleColor();
        } else {
            return config.getDownTriangleColor();
        }
    }
} 