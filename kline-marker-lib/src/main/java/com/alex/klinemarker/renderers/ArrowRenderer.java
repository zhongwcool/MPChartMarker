package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 箭头标记渲染器
 * 支持向上和向下箭头
 */
public class ArrowRenderer implements IMarkerRenderer {

    private final Paint arrowPaint;
    private final float density;

    public ArrowRenderer(float density) {
        this.density = density;

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 获取标记大小
        float size = marker.getConfig().getMarkerSize() * density / 2;

        // 设置颜色
        arrowPaint.setColor(marker.getConfig().getBackgroundColor());
        arrowPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 创建箭头路径
        Path arrowPath = createArrowPath(centerX, centerY, size, marker.getConfig().getShape());
        canvas.drawPath(arrowPath, arrowPaint);
    }

    /**
     * 创建箭头路径
     */
    private Path createArrowPath(float centerX, float centerY, float size, MarkerShape shape) {
        Path path = new Path();

        if (shape == MarkerShape.ARROW_UP) {
            // 向上箭头
            path.moveTo(centerX, centerY - size);              // 箭头顶点
            path.lineTo(centerX - size * 0.7f, centerY);       // 左侧角
            path.lineTo(centerX - size * 0.3f, centerY);       // 左侧箭身
            path.lineTo(centerX - size * 0.3f, centerY + size * 0.8f); // 左侧箭身底部
            path.lineTo(centerX + size * 0.3f, centerY + size * 0.8f); // 右侧箭身底部
            path.lineTo(centerX + size * 0.3f, centerY);       // 右侧箭身
            path.lineTo(centerX + size * 0.7f, centerY);       // 右侧角
        } else {
            // 向下箭头
            path.moveTo(centerX, centerY + size);              // 箭头顶点
            path.lineTo(centerX - size * 0.7f, centerY);       // 左侧角
            path.lineTo(centerX - size * 0.3f, centerY);       // 左侧箭身
            path.lineTo(centerX - size * 0.3f, centerY - size * 0.8f); // 左侧箭身顶部
            path.lineTo(centerX + size * 0.3f, centerY - size * 0.8f); // 右侧箭身顶部
            path.lineTo(centerX + size * 0.3f, centerY);       // 右侧箭身
            path.lineTo(centerX + size * 0.7f, centerY);       // 右侧角
        }

        path.close();
        return path;
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
        return shape == MarkerShape.ARROW_UP || shape == MarkerShape.ARROW_DOWN;
    }
} 