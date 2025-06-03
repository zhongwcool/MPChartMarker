package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;
import com.alex.klinemarker.utils.TextUtils;

/**
 * 箭头标记渲染器
 * 支持向上和向下箭头，并支持文字绘制
 */
public class ArrowRenderer implements IMarkerRenderer {

    private final Paint arrowPaint;
    private final Paint textPaint;
    private final float density;
    private final Rect textBounds = new Rect();

    public ArrowRenderer(float density) {
        this.density = density;

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        // 优化文字渲染质量，特别适合汉字显示
        textPaint.setSubpixelText(true);
        textPaint.setLinearText(true);
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

        // 绘制文字（如果需要）
        if (marker.getConfig().isShowText() && marker.getText() != null && !marker.getText().isEmpty()) {
            // 处理文字：限制长度（除了TEXT ONLY）
            String originalText = marker.getText();
            String displayText = TextUtils.processMarkerText(originalText, marker.getConfig().getShape());

            // 设置文字样式
            textPaint.setTextSize(marker.getConfig().getTextSize() * density);
            textPaint.setColor(marker.getConfig().getTextColor());

            // 使用改进的文字居中算法，特别优化汉字显示
            float textY = TextUtils.calculateChineseTextBaselineY(textPaint, displayText, centerY);
            canvas.drawText(displayText, centerX, textY, textPaint);
        }
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