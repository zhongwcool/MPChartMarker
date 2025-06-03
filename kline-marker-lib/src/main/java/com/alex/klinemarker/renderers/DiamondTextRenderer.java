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
 * 菱形背景 + 文字标记渲染器
 */
public class DiamondTextRenderer implements IMarkerRenderer {

    private final Paint backgroundPaint;
    private final Paint textPaint;
    private final float density;
    private final Rect textBounds = new Rect();

    public DiamondTextRenderer(float density) {
        this.density = density;

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

        // 计算菱形大小 - 修复菱形过小问题
        float size = marker.getConfig().getMarkerSize() * density * 0.6f; // 从/2改为*0.7f，让菱形更大

        // 创建菱形路径
        Path diamondPath = new Path();
        diamondPath.moveTo(centerX, centerY - size);      // 上
        diamondPath.lineTo(centerX + size, centerY);      // 右
        diamondPath.lineTo(centerX, centerY + size);      // 下
        diamondPath.lineTo(centerX - size, centerY);      // 左
        diamondPath.close();

        // 绘制菱形背景
        backgroundPaint.setColor(marker.getConfig().getBackgroundColor());
        backgroundPaint.setAlpha((int) (marker.getConfig().getAlpha() * 255));
        canvas.drawPath(diamondPath, backgroundPaint);

        // 绘制文字
        if (marker.getConfig().isShowText() && !displayText.isEmpty()) {
            // 使用改进的文字居中算法，特别优化汉字显示
            float textY = TextUtils.calculateChineseTextBaselineY(textPaint, displayText, centerY);
            canvas.drawText(displayText, centerX, textY, textPaint);
        }
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
        return shape == MarkerShape.DIAMOND;
    }
} 