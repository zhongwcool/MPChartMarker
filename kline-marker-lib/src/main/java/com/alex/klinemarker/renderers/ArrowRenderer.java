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
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context) {
        // 获取标准标记大小
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        float size = markerSize * density / 2;

        // 设置颜色
        int color = marker.getColor() != 0 ? marker.getColor() : getDefaultColor(marker, config);
        arrowPaint.setColor(color);

        // 创建箭头路径
        Path arrowPath = createArrowPath(centerX, centerY, size, marker.getStyle());
        canvas.drawPath(arrowPath, arrowPaint);
    }

    /**
     * 创建箭头路径 - 增大视觉大小
     */
    private Path createArrowPath(float centerX, float centerY, float size, MarkerStyle style) {
        Path path = new Path();

        if (style == MarkerStyle.ARROW_UP) {
            // 向上箭头 - 增大视觉大小
            path.moveTo(centerX, centerY - size);              // 箭头顶点
            path.lineTo(centerX - size * 0.7f, centerY);       // 左侧角 (增大到0.7)
            path.lineTo(centerX - size * 0.3f, centerY);       // 左侧箭身 (增大到0.3)
            path.lineTo(centerX - size * 0.3f, centerY + size * 0.8f); // 左侧箭身底部 (增长到0.8)
            path.lineTo(centerX + size * 0.3f, centerY + size * 0.8f); // 右侧箭身底部
            path.lineTo(centerX + size * 0.3f, centerY);       // 右侧箭身
            path.lineTo(centerX + size * 0.7f, centerY);       // 右侧角
        } else {
            // 向下箭头 - 增大视觉大小
            path.moveTo(centerX, centerY + size);              // 箭头顶点
            path.lineTo(centerX - size * 0.7f, centerY);       // 左侧角 (增大到0.7)
            path.lineTo(centerX - size * 0.3f, centerY);       // 左侧箭身 (增大到0.3)
            path.lineTo(centerX - size * 0.3f, centerY - size * 0.8f); // 左侧箭身顶部 (增长到0.8)
            path.lineTo(centerX + size * 0.3f, centerY - size * 0.8f); // 右侧箭身顶部
            path.lineTo(centerX + size * 0.3f, centerY);       // 右侧箭身
            path.lineTo(centerX + size * 0.7f, centerY);       // 右侧角
        }

        path.close();
        return path;
    }

    @Override
    public float getMarkerWidth(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 统一大小，不再使用1.2倍数
    }

    @Override
    public float getMarkerHeight(MarkerData marker, MarkerConfig config) {
        float markerSize = marker.getSize() > 0 ? marker.getSize() : config.getMarkerSize();
        return markerSize * density; // 统一大小，不再使用1.8倍数
    }

    @Override
    public boolean supportsStyle(MarkerStyle style) {
        return style == MarkerStyle.ARROW_UP || style == MarkerStyle.ARROW_DOWN;
    }

    private int getDefaultColor(MarkerData marker, MarkerConfig config) {
        if (marker.getStyle() == MarkerStyle.ARROW_UP) {
            return 0xFF4CAF50; // 绿色
        } else {
            return 0xFFFF4444; // 红色
        }
    }
} 