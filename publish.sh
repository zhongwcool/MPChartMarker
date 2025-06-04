#!/bin/bash

# KLine Marker Library 发布脚本
# 用法: ./publish.sh <version> [github_username]
# 例如: ./publish.sh 1.0.1 zhongwcool

set -e

# 获取参数
VERSION=$1
GITHUB_USERNAME=${2:-"zhongwcool"}

# 检查版本号是否提供
if [ -z "$VERSION" ]; then
    echo "❌ 请指定版本号！"
    echo ""
    echo "用法:"
    echo "  ./publish.sh <版本号> [GitHub用户名]"
    echo ""
    echo "示例:"
    echo "  ./publish.sh 1.0.0"
    echo "  ./publish.sh 1.0.1 zhongwcool"
    echo ""
    echo "建议的版本号格式:"
    echo "  - 主要版本: 1.0.0, 2.0.0"
    echo "  - 功能更新: 1.1.0, 1.2.0"
    echo "  - Bug修复: 1.0.1, 1.0.2"
    exit 1
fi

echo "🚀 开始发布 KLine Marker Library v${VERSION}"
echo "📂 GitHub 用户: ${GITHUB_USERNAME}"

# 检查是否有未提交的更改
if ! git diff-index --quiet HEAD --; then
    echo "❌ 发现未提交的更改，请先提交所有更改"
    exit 1
fi

# 更新版本号
echo "📝 更新版本号为 ${VERSION}"
sed -i "s/version = \".*\"/version = \"${VERSION}\"/" kline-marker-lib/build.gradle.kts

# 提交版本更新
echo "💾 提交版本更新"
git add kline-marker-lib/build.gradle.kts
git commit -m "发布版本 v${VERSION}"

# 创建标签
echo "🏷️ 创建 Git 标签"
git tag -a "v${VERSION}" -m "发布版本 v${VERSION}"

# 推送到远程仓库
echo "⬆️ 推送到远程仓库"
git push origin main
git push origin "v${VERSION}"

# 构建和发布到本地 Maven 仓库
echo "🔨 构建库"
./gradlew :kline-marker-lib:clean
./gradlew :kline-marker-lib:assembleRelease

echo "📦 发布到本地 Maven 仓库"
./gradlew :kline-marker-lib:publishToMavenLocal

# 验证发布
LOCAL_PATH="$HOME/.m2/repository/com/alex/klinemarker/kline-marker-lib/${VERSION}"
if [ -d "$LOCAL_PATH" ]; then
    echo "✅ 本地发布成功！"
    echo "📁 文件位置: $LOCAL_PATH"
    ls -la "$LOCAL_PATH"
else
    echo "❌ 本地发布失败"
    exit 1
fi

echo ""
echo "🎉 发布完成！"
echo ""
echo "📋 下一步操作："
echo "1. 访问 https://jitpack.io"
echo "2. 输入仓库地址: ${GITHUB_USERNAME}/MPChartMarker"
echo "3. 选择标签: v${VERSION}"
echo "4. 点击 'Get it' 等待构建完成"
echo ""
echo "📚 JitPack 使用方式："
echo "repositories {"
echo "    maven { url = uri(\"https://jitpack.io\") }"
echo "}"
echo "dependencies {"
echo "    implementation(\"com.github.${GITHUB_USERNAME}:MPChartMarker:v${VERSION}\")"
echo "}"
echo ""
echo "🔗 本地测试："
echo "repositories {"
echo "    mavenLocal()"
echo "}"
echo "dependencies {"
echo "    implementation(\"com.alex.klinemarker:kline-marker-lib:${VERSION}\")"
echo "}"
echo ""
echo "⚠️  重要提醒："
echo "请确保修改 kline-marker-lib/build.gradle.kts 中的以下信息："
echo "- url.set(\"https://github.com/${GITHUB_USERNAME}/MPChartMarker\")"
echo "- developer id, name, email"
echo "- scm 相关的 GitHub 地址" 