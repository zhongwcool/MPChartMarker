package com.alex.mpchart.sample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.alex.mpchart.sample.databinding.FragmentSecondBinding;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    private Markwon markwon;

    private static final String README_URL = "https://raw.githubusercontent.com/zhongwcool/MPChartMarker/refs/heads/main/README.md";

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

        // 初始化组件
        initializeComponents();

        // 设置返回按钮
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        // 加载README文档
        loadReadmeFromGitHub();
    }

    private void initializeComponents() {
        // 初始化HTTP客户端
        httpClient = new OkHttpClient.Builder()
                .build();

        // 初始化线程池
        executorService = Executors.newSingleThreadExecutor();

        // 初始化Markwon
        markwon = Markwon.builder(requireContext())
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .build();
    }

    private void loadReadmeFromGitHub() {
        // 显示进度条
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textviewSecond.setText("正在从GitHub加载README文档...");

        Request request = new Request.Builder()
                .url(README_URL)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 在主线程中更新UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.textviewSecond.setText("加载失败：" + e.getMessage() + "\n\n请检查网络连接后重试。");
                        Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String markdownContent = response.body().string();

                    // 在主线程中更新UI
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            renderMarkdown(markdownContent);
                        });
                    }
                } else {
                    // 在主线程中更新UI
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.textviewSecond.setText("加载失败：HTTP " + response.code() + "\n\n请稍后重试。");
                            Toast.makeText(getContext(), "服务器响应错误", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
                response.close();
            }
        });
    }

    private void renderMarkdown(String markdownContent) {
        try {
            // 使用Markwon渲染Markdown内容
            markwon.setMarkdown(binding.textviewSecond, markdownContent);

            // 设置TextView的样式
            binding.textviewSecond.setTextSize(14);
            binding.textviewSecond.setPadding(16, 16, 16, 16);

            Toast.makeText(getContext(), "README文档加载完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            binding.textviewSecond.setText("渲染Markdown失败：" + e.getMessage());
            Toast.makeText(getContext(), "文档渲染失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 清理资源
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        binding = null;
    }
}