package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.core.MarkerConfig;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 自定义图标标记渲染器
 * 支持使用Drawable作为标记图标
 */
public class CustomIconRenderer implements IMarkerRenderer {

    private final float density;

    public CustomIconRenderer(float density) {
        this.density = density;
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        Drawable icon = marker.getCustomIcon();
        if (icon == null) {
            return; // 没有自定义图标，不绘制
        }

        // 获取标准标记大小，所有自定义图标使用统一的标准大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        int iconSize = (int) (markerSize * density); // 使用1.0x标准大小，与圆形、菱形一致

        // 计算图标位置
        int left = (int) (centerX - iconSize / 2);
        int top = (int) (centerY - iconSize / 2);
        int right = left + iconSize;
        int bottom = top + iconSize;

        // 设置图标边界并绘制
        icon.setBounds(left, top, right, bottom);
        icon.draw(canvas);
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 与圆形、菱形标记保持一致
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 与圆形、菱形标记保持一致
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.CUSTOM_ICON;
    }
} 