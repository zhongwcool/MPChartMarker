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
 * 菱形标记渲染器
 * 只绘制菱形图形，不显示文字
 */
public class DiamondTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final float density;

    public DiamondTextRenderer(float density) {
        this.density = density;

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float halfSize = markerSize * density / 2;

        // 设置背景颜色
        int bgColor = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker, config);
        backgroundPaint.setColor(bgColor);

        // 绘制菱形
        Path diamondPath = new Path();
        diamondPath.moveTo(centerX, centerY - halfSize);     // 上
        diamondPath.lineTo(centerX + halfSize, centerY);     // 右
        diamondPath.lineTo(centerX, centerY + halfSize);     // 下
        diamondPath.lineTo(centerX - halfSize, centerY);     // 左
        diamondPath.close();

        canvas.drawPath(diamondPath, backgroundPaint);

        // 不绘制文字，只显示菱形图形
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
        return style == MarkerStyle.DIAMOND_TEXT;
    }

    private int getDefaultColor(MarkerData marker, MarkerConfig config) {
        switch (marker.getType()) {
            case STOP_LOSS:
                return 0xFFE91E63; // 粉红色
            case TAKE_PROFIT:
                return 0xFF4CAF50; // 绿色
            default:
                return config.getNumberColor();
        }
    }
} 