package com.alex.klinemarker.core;

import android.content.Context;

import com.alex.klinemarker.data.MarkerShape;
import com.alex.klinemarker.renderers.ArrowRenderer;
import com.alex.klinemarker.renderers.CircleTextRenderer;
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
 * 负责创建和管理不同形状的标记渲染器
 */
public class MarkerRendererFactory {

    private final Map<MarkerShape, IMarkerRenderer> renderers;
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
        renderers.put(MarkerShape.RECTANGLE, new RectangleTextRenderer(density));

        // 圆形背景 + 文字
        renderers.put(MarkerShape.CIRCLE, new CircleTextRenderer(density));

        // 三角形（共用一个渲染器）
        TriangleRenderer triangleRenderer = new TriangleRenderer(density);
        renderers.put(MarkerShape.TRIANGLE_UP, triangleRenderer);
        renderers.put(MarkerShape.TRIANGLE_DOWN, triangleRenderer);

        // 纯文字
        renderers.put(MarkerShape.NONE, new TextOnlyRenderer(density));

        // 菱形背景 + 文字
        renderers.put(MarkerShape.DIAMOND, new DiamondTextRenderer(density));

        // 几何图形渲染器
        renderers.put(MarkerShape.STAR, new StarRenderer(density));
        renderers.put(MarkerShape.DOT, new DotRenderer(density));

        // 箭头渲染器（共用一个渲染器）
        ArrowRenderer arrowRenderer = new ArrowRenderer(density);
        renderers.put(MarkerShape.ARROW_UP, arrowRenderer);
        renderers.put(MarkerShape.ARROW_DOWN, arrowRenderer);

        // 自定义图标渲染器
        renderers.put(MarkerShape.CUSTOM_ICON, new CustomIconRenderer(density));
    }

    /**
     * 获取指定形状的渲染器
     *
     * @param shape 标记形状
     * @return 对应的渲染器，如果没有找到则返回null
     */
    public IMarkerRenderer getRenderer(MarkerShape shape) {
        return renderers.get(shape);
    }

    /**
     * 注册自定义渲染器
     *
     * @param shape    标记形状
     * @param renderer 渲染器实例
     */
    public void registerRenderer(MarkerShape shape, IMarkerRenderer renderer) {
        renderers.put(shape, renderer);
    }

    /**
     * 检查是否支持指定的形状
     *
     * @param shape 标记形状
     * @return 是否支持
     */
    public boolean supportsShape(MarkerShape shape) {
        return renderers.containsKey(shape);
    }
} 