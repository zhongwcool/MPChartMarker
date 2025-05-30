package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;

/**
 * 自定义图标标记渲染器
 * 支持绘制自定义Drawable图标
 */
public class CustomIconRenderer implements IMarkerRenderer {

    private final float density;

    public CustomIconRenderer(float density) {
        this.density = density;
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        Drawable icon = marker.getConfig().getCustomIcon();
        if (icon == null) {
            return;
        }

        // 获取图标大小
        int size = (int) (marker.getConfig().getMarkerSize() * density);
        int halfSize = size / 2;

        // 设置图标边界
        icon.setBounds(
                (int) (centerX - halfSize),
                (int) (centerY - halfSize),
                (int) (centerX + halfSize),
                (int) (centerY + halfSize)
        );

        // 设置透明度
        icon.setAlpha((int) (marker.getConfig().getAlpha() * 255));

        // 绘制图标
        icon.draw(canvas);
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
        return shape == MarkerShape.CUSTOM_ICON;
    }
} 