package com.alex.klinemarker.core;

import android.content.Context;

import com.alex.klinemarker.data.MarkerStyle;
import com.alex.klinemarker.renderers.ArrowRenderer;
import com.alex.klinemarker.renderers.CircleTextRenderer;
import com.alex.klinemarker.renderers.CrossRenderer;
import com.alex.klinemarker.renderers.CustomIconRenderer;
import com.alex.klinemarker.renderers.DiamondTextRenderer;
import com.alex.klinemarker.renderers.DotRenderer;
import com.alex.klinemarker.renderers.RectangleTextRenderer;
import com.alex.klinemarker.renderers.StarRenderer;
import com.alex.klinemarker.renderers.TextOnlyRenderer;
import com.alex.klinemarker.renderers.TriangleRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * 标记渲染器工厂
 * 负责创建和管理不同样式的标记渲染器
 */
public class MarkerRendererFactory {

    private final Map<MarkerStyle, IMarkerRenderer> renderers;
    private final float density;

    public MarkerRendererFactory(Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        this.renderers = new HashMap<>();

        // 初始化所有渲染器
        initRenderers();
    }

    /**
     * 初始化所有内置渲染器
     */
    private void initRenderers() {
        // 矩形背景 + 文字
        renderers.put(MarkerStyle.RECTANGLE_TEXT, new RectangleTextRenderer(density));

        // 圆形背景 + 文字
        renderers.put(MarkerStyle.CIRCLE_TEXT, new CircleTextRenderer(density));

        // 三角形（共用一个渲染器）
        TriangleRenderer triangleRenderer = new TriangleRenderer(density);
        renderers.put(MarkerStyle.TRIANGLE_UP, triangleRenderer);
        renderers.put(MarkerStyle.TRIANGLE_DOWN, triangleRenderer);

        // 纯文字
        renderers.put(MarkerStyle.TEXT_ONLY, new TextOnlyRenderer(density));

        // 菱形背景 + 文字
        renderers.put(MarkerStyle.DIAMOND_TEXT, new DiamondTextRenderer(density));

        // 几何图形渲染器
        renderers.put(MarkerStyle.STAR, new StarRenderer(density));
        renderers.put(MarkerStyle.DOT, new DotRenderer(density));
        renderers.put(MarkerStyle.CROSS, new CrossRenderer(density));

        // 箭头渲染器（共用一个渲染器）
        ArrowRenderer arrowRenderer = new ArrowRenderer(density);
        renderers.put(MarkerStyle.ARROW_UP, arrowRenderer);
        renderers.put(MarkerStyle.ARROW_DOWN, arrowRenderer);

        // 自定义图标渲染器
        renderers.put(MarkerStyle.CUSTOM_ICON, new CustomIconRenderer(density));
    }

    /**
     * 获取指定样式的渲染器
     *
     * @param style 标记样式
     * @return 对应的渲染器，如果没有找到则返回null
     */
    public IMarkerRenderer getRenderer(MarkerStyle style) {
        return renderers.get(style);
    }

    /**
     * 注册自定义渲染器
     *
     * @param style    标记样式
     * @param renderer 渲染器实例
     */
    public void registerRenderer(MarkerStyle style, IMarkerRenderer renderer) {
        renderers.put(style, renderer);
    }

    /**
     * 检查是否支持指定的样式
     *
     * @param style 标记样式
     * @return 是否支持
     */
    public boolean supportsStyle(MarkerStyle style) {
        return renderers.containsKey(style);
    }
} 