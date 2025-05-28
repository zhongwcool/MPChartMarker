package com.alex.mpchart.sample;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.alex.mpchart.sample.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置使用指南内容
        setupUsageGuide();

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    private void setupUsageGuide() {
        String htmlContent =
                "<h2 style='color:#2196F3;'>K线标记库使用指南</h2>" +

                        "<h3 style='color:#4CAF50;'>✨ 主要功能</h3>" +
                        "<ul>" +
                        "<li><strong>多种标记类型</strong>：买入、卖出、数字、上三角、下三角</li>" +
                        "<li><strong>趋势区间背景</strong>：上涨、下跌、中性趋势的精美阴影效果</li>" +
                        "<li><strong>完全可自定义</strong>：外观配置、颜色、大小、位置等</li>" +
                        "<li><strong>高性能渲染</strong>：只绘制可见区域的标记和趋势区间</li>" +
                        "</ul>" +

                        "<h3 style='color:#FF9800;'>🔧 集成步骤</h3>" +
                        "<ol>" +
                        "<li><strong>添加依赖</strong><br/>" +
                        "<code>implementation(project(\":kline-marker-lib\"))<br/>" +
                        "implementation(\"com.github.PhilJay:MPAndroidChart:v3.1.0\")</code></li>" +

                        "<li><strong>创建数据适配器</strong><br/>" +
                        "<code>public class MyDataAdapter implements KLineDataAdapter&lt;MyData&gt; { ... }</code></li>" +

                        "<li><strong>初始化标记管理器</strong><br/>" +
                        "<code>KLineMarkerManager&lt;MyData&gt; manager = <br/>" +
                        "    new KLineMarkerManager.Builder&lt;&gt;()<br/>" +
                        "        .context(this)<br/>" +
                        "        .chart(combinedChart)<br/>" +
                        "        .dataAdapter(adapter)<br/>" +
                        "        .build();</code></li>" +
                        "</ol>" +

                        "<h3 style='color:#9C27B0;'>📊 标记类型说明</h3>" +
                        "<ul>" +
                        "<li><strong style='color:#4CAF50;'>BUY</strong> - 买入标记（绿色圆角矩形）</li>" +
                        "<li><strong style='color:#F44336;'>SELL</strong> - 卖出标记（红色圆角矩形）</li>" +
                        "<li><strong style='color:#2196F3;'>NUMBER</strong> - 数字标记（蓝色圆形）</li>" +
                        "<li><strong style='color:#FF9800;'>UP_TRIANGLE</strong> - 上三角（橙色▲）</li>" +
                        "<li><strong style='color:#FF9800;'>DOWN_TRIANGLE</strong> - 下三角（橙色▼）</li>" +
                        "</ul>" +

                        "<h3 style='color:#607D8B;'>🌈 趋势区间类型</h3>" +
                        "<ul>" +
                        "<li><strong style='color:#4CAF50;'>RISING</strong> - 上涨趋势（绿色渐变背景）</li>" +
                        "<li><strong style='color:#F44336;'>FALLING</strong> - 下跌趋势（红色渐变背景）</li>" +
                        "<li><strong style='color:#2196F3;'>NEUTRAL</strong> - 中性趋势（蓝色渐变背景）</li>" +
                        "</ul>" +

                        "<h3 style='color:#795548;'>💡 使用技巧</h3>" +
                        "<ul>" +
                        "<li>使用 <code>MarkerConfig.Builder()</code> 自定义标记外观</li>" +
                        "<li>使用 <code>TrendRegionConfig.Builder()</code> 自定义趋势区间样式</li>" +
                        "<li>调用 <code>refresh()</code> 方法刷新图表显示</li>" +
                        "<li>可以动态添加、删除标记和趋势区间</li>" +
                        "<li>支持触摸交互和缩放操作</li>" +
                        "</ul>" +

                        "<h3 style='color:#E91E63;'>🎨 高级配置示例</h3>" +
                        "<pre style='background:#F5F5F5; padding:10px; border-radius:5px;'>" +
                        "MarkerConfig config = new MarkerConfig.Builder()<br/>" +
                        "    .markerSize(12f)<br/>" +
                        "    .buyColor(Color.parseColor(\"#4CAF50\"))<br/>" +
                        "    .sellColor(Color.parseColor(\"#F44336\"))<br/>" +
                        "    .build();<br/><br/>" +

                        "TrendRegionConfig trendConfig = <br/>" +
                        "    new TrendRegionConfig.Builder()<br/>" +
                        "        .risingColor(Color.parseColor(\"#4CAF50\"))<br/>" +
                        "        .alpha(60)<br/>" +
                        "        .enableBezierCurve(true)<br/>" +
                        "        .enableGradient(true)<br/>" +
                        "        .build();" +
                        "</pre>" +

                        "<br/><p style='text-align:center; color:#666; font-style:italic;'>" +
                        "返回上一页面体验实际功能演示 ↩️</p>";

        TextView textView = binding.textviewSecond;
        textView.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT));
        textView.setTextSize(14);
        textView.setPadding(16, 16, 16, 16);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}