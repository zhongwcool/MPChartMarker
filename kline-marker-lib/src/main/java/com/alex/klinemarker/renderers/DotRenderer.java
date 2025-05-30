package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 圆点标记渲染器
 * 绘制简单的圆点标记
 */
public class DotRenderer implements IMarkerRenderer {

    private final Paint dotPaint;
    private final float density;

    public DotRenderer(float density) {
        this.density = density;

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 计算圆点半径 - 修复圆点过小问题
        float radius = marker.getConfig().getMarkerSize() * density / 3f; // 从/4改为/3f，让圆点更大但仍保持小于其他标记

        // 设置颜色
        dotPaint.setColor(marker.getConfig().getBackgroundColor());
        dotPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 绘制圆点
        canvas.drawCircle(centerX, centerY, radius, dotPaint);
    }

    @Override
    public float getMarkerWidth(MarkerData marker) {
        return marker.getConfig().getMarkerSize() * density / 1.5f; // 相应调整宽度
    }

    @Override
    public float getMarkerHeight(MarkerData marker) {
        return marker.getConfig().getMarkerSize() * density / 1.5f; // 相应调整高度
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.DOT;
    }
} 