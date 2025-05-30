package com.alex.klinemarker.core;

import android.content.Context;
import android.graphics.Canvas;

import com.alex.klinemarker.data.MarkerData;
import com.alex.klinemarker.data.MarkerStyle;

/**
 * 标记渲染器接口
 * 定义标记渲染的基本方法
 */
public interface IMarkerRenderer {

    /**
     * 绘制标记
     *
     * @param canvas  画布
     * @param centerX 标记中心X坐标
     * @param centerY 标记中心Y坐标
     * @param marker  标记数据
     * @param config  标记配置
     * @param context 上下文，用于计算虚线长度
     */
    void drawMarker(Canvas canvas, float centerX, float centerY, MarkerData marker, MarkerConfig config, Context context);

    /**
     * 获取标记的实际宽度
     *
     * @param marker 标记数据
     * @param config 标记配置
     * @return 标记宽度（像素）
     */
    float getMarkerWidth(MarkerData marker, MarkerConfig config);

    /**
     * 获取标记的实际高度
     *
     * @param marker 标记数据
     * @param config 标记配置
     * @return 标记高度（像素）
     */
    float getMarkerHeight(MarkerData marker, MarkerConfig config);

    /**
     * 判断是否支持指定的标记样式
     *
     * @param style 标记样式
     * @return 是否支持
     */
    boolean supportsStyle(MarkerStyle style);
} 