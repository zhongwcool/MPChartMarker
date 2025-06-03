package com.alex.klinemarker.renderers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.alex.klinemarker.core.IMarkerRenderer;
import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerShape;
import com.alex.klinemarker.utils.TextUtils;

/**
 * 矩形背景 + 文字标记渲染器
 * 直角正方形大小固定为16dp，不支持调整
 */
public class RectangleTextRenderer implements IMarkerRenderer {

    private static final float FIXED_SQUARE_SIZE_DP = 12f; // 固定直角正方形大小为12dp
    
    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;
    private final float fixedSquareSize; // 固定的正方形尺寸

    public RectangleTextRenderer(float density) {
        this.density = density;
        this.fixedSquareSize = FIXED_SQUARE_SIZE_DP * density; // 固定尺寸

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        // 优化文字渲染质量，特别适合汉字显示
        textPaint.setSubpixelText(true);
        textPaint.setLinearText(true);
    }

    @Override
    public void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, Context context) {
        // 处理文字：限制长度（除了TEXT ONLY）
        String originalText = marker.getText();
        String displayText = TextUtils.processMarkerText(originalText, marker.getConfig().getShape());

        if (displayText == null || displayText.isEmpty()) {
            displayText = "";
        }

        // 设置文字样式
        textPaint.setTextSize(marker.getConfig().getTextSize() * density);
        textPaint.setColor(marker.getConfig().getTextColor());

        // 使用固定的正方形尺寸，不受markerSize影响
        float halfSize = fixedSquareSize / 2;

        // 计算固定正方形位置
        RectF rect = new RectF(
                centerX - halfSize,
                centerY - halfSize,
                centerX + halfSize,
                centerY + halfSize
        );

        // 绘制直角矩形背景（移除圆角）
        backgroundPaint.setColor(marker.getConfig().getBackgroundColor());
        backgroundPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));
        canvas.drawRect(rect, backgroundPaint);

        // 绘制文字
        if (marker.getConfig().isShowText() && !displayText.isEmpty()) {
            // 使用改进的文字居中算法，特别优化汉字显示
            float textY = TextUtils.calculateChineseTextBaselineY(textPaint, displayText, centerY);
            canvas.drawText(displayText, centerX, textY, textPaint);
        }
    }

    @Override
    public float getMarkerWidth(MarkerData marker) {
        // 返回固定宽度，不受markerSize影响
        return fixedSquareSize;
    }

    @Override
    public float getMarkerHeight(MarkerData marker) {
        // 返回固定高度，不受markerSize影响
        return fixedSquareSize;
    }

    @Override
    public boolean supportsShape(MarkerShape shape) {
        return shape == MarkerShape.RECTANGLE;
    }
} 