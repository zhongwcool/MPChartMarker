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
 * 五角星标记渲染器
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
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标准标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float radius = markerSize * density / 2;

        // 设置颜色
        int color = marker.getColor() != 0 ? marker.getColor() : 0xFFFFD700; // 默认金色
        starPaint.setColor(color);

        // 绘制五角星
        Path starPath = createStarPath(centerX, centerY, radius);
        canvas.drawPath(starPath, starPaint);
    }

    /**
     * 创建五角星路径
     */
    private Path createStarPath(float centerX, float centerY, float outerRadius) {
        Path path = new Path();
        float innerRadius = outerRadius * 0.4f; // 内半径为外半径的40%

        double angleStep = Math.PI / 5; // 36度
        double startAngle = -Math.PI / 2; // 从正上方开始

        // 五角星有10个点（5个外角点 + 5个内角点）
        for (int i = 0; i < 10; i++) {
            double angle = startAngle + i * angleStep;
            float radius = (i % 2 == 0) ? outerRadius : innerRadius;

            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        path.close();
        return path;
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
        return style == MarkerStyle.STAR;
    }
} 