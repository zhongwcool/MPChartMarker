package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 圆点标记渲染器
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
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标准标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float radius = markerSize * density / 2;

        // 设置颜色
        int color = marker.getColor() != 0 ? marker.getColor() : 0xFF888888; // 默认灰色
        dotPaint.setColor(color);

        // 绘制圆点
        canvas.drawCircle(centerX, centerY, radius, dotPaint);
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
        return style == MarkerStyle.DOT;
    }
} 