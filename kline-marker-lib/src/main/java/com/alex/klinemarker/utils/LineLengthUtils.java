package com.alex.klinemarker.utils;

import android.content.Context;
import android.util.TypedValue;

import com.alex.klinemarker.data.LineLength;

/**
 * 虚线长度工具类
 * 用于将LineLength枚举转换为标记与K线数据点之间的距离像素值
 */
public class LineLengthUtils {

    /**
     * 将虚线长度类型转换为像素值
     *
     * @param context    上下文
     * @param lineLength 虚线长度类型
     * @return 像素长度，NONE表示极短距离
     */
    public static float getLineLengthInPixels(Context context, LineLength lineLength) {
        if (lineLength == null) {
            return dpToPixels(context, 20f); // 默认中等距离
        }

        float dpValue;
        switch (lineLength) {
            case NONE:
                dpValue = 8f;   // 短距离但不显示虚线，从3f改为8f
                break;
            case SHORT:
                dpValue = 12f;   // 短距离，从8f改为12f
                break;
            case MEDIUM:
                dpValue = 20f;  // 中等距离（默认）
                break;
            case LONG:
                dpValue = 35f;  // 长距离
                break;
            case EXTRA_LONG:
                dpValue = 55f;  // 超长距离
                break;
            default:
                dpValue = 20f;  // 默认中等距离
                break;
        }

        return dpToPixels(context, dpValue);
    }

    /**
     * dp转换为像素
     *
     * @param context 上下文
     * @param dp      dp值
     * @return 像素值
     */
    private static float dpToPixels(Context context, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    /**
     * 检查是否需要绘制虚线
     * NONE类型不绘制虚线，其他类型都绘制虚线
     *
     * @param lineLength 虚线长度类型
     * @return 是否需要绘制连接线
     */
    public static boolean shouldDrawLine(LineLength lineLength) {
        return lineLength != null && lineLength != LineLength.NONE; // NONE类型不绘制连接线
    }
} 