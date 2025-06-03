package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 三角形标记渲染器
 * 支持向上和向下三角形，不支持文字绘制（纯图标）
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
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 获取标记大小
        float size = marker.getConfig().getMarkerSize() * density / 2;

        // 设置颜色
        trianglePaint.setColor(marker.getConfig().getBackgroundColor());
        trianglePaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 创建三角形路径
        Path trianglePath = createTrianglePath(centerX, centerY, size, marker.getConfig().getShape());
        canvas.drawPath(trianglePath, trianglePaint);

        // 三角形不支持文字显示
    }

    /**
     * 创建三角形路径
     */
    private Path createTrianglePath(float centerX, float centerY, float size, MarkerShape shape) {
        Path path = new Path();

        if (shape == MarkerShape.TRIANGLE_UP) {
            // 向上三角形
            path.moveTo(centerX, centerY - size);          // 顶点
            path.lineTo(centerX - size, centerY + size);   // 左下
            path.lineTo(centerX + size, centerY + size);   // 右下
            path.close();
        } else if (shape == MarkerShape.TRIANGLE_DOWN) {
            // 向下三角形
            path.moveTo(centerX, centerY + size);          // 底点
            path.lineTo(centerX - size, centerY - size);   // 左上
            path.lineTo(centerX + size, centerY - size);   // 右上
            path.close();
        }

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
        return shape == MarkerShape.TRIANGLE_UP || shape == MarkerShape.TRIANGLE_DOWN;
    }
} 