package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 五角星标记渲染器
 * 绘制五角星形状
 */
public class StarRenderer implements IMarkerRenderer {

    private final Paint starPaint;
    private final float density;

    public StarRenderer(float density) {
        this.density = density;

        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 获取标记大小 - 修复五角星过小问题
        float radius = marker.getConfig().getMarkerSize() * density / 2f;

        // 设置颜色
        starPaint.setColor(marker.getConfig().getBackgroundColor());
        starPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 创建五角星路径
        Path starPath = createStarPath(centerX, centerY, radius);
        canvas.drawPath(starPath, starPaint);
    }

    /**
     * 创建五角星路径
     */
    private Path createStarPath(float centerX, float centerY, float radius) {
        Path path = new Path();
        float innerRadius = radius * 0.4f;

        // 五角星的5个外顶点和5个内顶点
        double angleStep = Math.PI / 5; // 36度
        double startAngle = -Math.PI / 2; // 从顶部开始

        boolean isOuter = true;
        for (int i = 0; i <= 10; i++) {
            double angle = startAngle + i * angleStep;
            float r = isOuter ? radius : innerRadius;
            float x = centerX + (float) (r * Math.cos(angle));
            float y = centerY + (float) (r * Math.sin(angle));

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            isOuter = !isOuter;
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
        return shape == MarkerShape.STAR;
    }
} 