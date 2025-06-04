# KLine Marker Library 发布脚本 (PowerShell)
# 用法: .\publish.ps1 [version] [github_username]
# 例如: .\publish.ps1 1.0.1 zhongwcool

param(
    [string]$Version,
    [string]$GitHubUsername = "zhongwcool"
)

$ErrorActionPreference = "Stop"

# 检查版本号是否提供
if (-not $Version) {
    Write-Host "❌ 请指定版本号！" -ForegroundColor Red
    Write-Host ""
    Write-Host "用法:" -ForegroundColor Yellow
    Write-Host "  .\publish.ps1 <版本号> [GitHub用户名]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Yellow
    Write-Host "  .\publish.ps1 1.0.0" -ForegroundColor Cyan
    Write-Host "  .\publish.ps1 1.0.1 zhongwcool" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "建议的版本号格式:" -ForegroundColor Yellow
    Write-Host "  - 主要版本: 1.0.0, 2.0.0" -ForegroundColor Gray
    Write-Host "  - 功能更新: 1.1.0, 1.2.0" -ForegroundColor Gray
    Write-Host "  - Bug修复: 1.0.1, 1.0.2" -ForegroundColor Gray
    exit 1
}

Write-Host "🚀 开始发布 KLine Marker Library v$Version" -ForegroundColor Green
Write-Host "📂 GitHub 用户: $GitHubUsername" -ForegroundColor Cyan

# 检查是否有未提交的更改
$gitStatus = git status --porcelain
if ($gitStatus) {
    Write-Host "❌ 发现未提交的更改，请先提交所有更改" -ForegroundColor Red
    Write-Host $gitStatus
    exit 1
}

# 更新版本号
Write-Host "📝 更新版本号为 $Version" -ForegroundColor Yellow
$buildFile = "kline-marker-lib\build.gradle.kts"
$content = Get-Content $buildFile -Raw
$content = $content -replace 'version = ".*"', "version = `"$Version`""
Set-Content $buildFile -Value $content

# 提交版本更新
Write-Host "💾 提交版本更新" -ForegroundColor Cyan
git add $buildFile
git commit -m "发布版本 v$Version"

# 创建标签
Write-Host "🏷️ 创建 Git 标签" -ForegroundColor Magenta
git tag -a "v$Version" -m "发布版本 v$Version"

# 推送到远程仓库
Write-Host "⬆️ 推送到远程仓库" -ForegroundColor Blue
git push origin main
git push origin "v$Version"

# 构建和发布到本地 Maven 仓库
Write-Host "🔨 构建库" -ForegroundColor White
.\gradlew :kline-marker-lib:clean
.\gradlew :kline-marker-lib:assembleRelease

Write-Host "📦 发布到本地 Maven 仓库" -ForegroundColor DarkGreen
.\gradlew :kline-marker-lib:publishToMavenLocal

# 验证发布
$localPath = "$env:USERPROFILE\.m2\repository\com\alex\klinemarker\kline-marker-lib\$Version"
if (Test-Path $localPath) {
    Write-Host "✅ 本地发布成功！" -ForegroundColor Green
    Write-Host "📁 文件位置: $localPath" -ForegroundColor Gray
    Get-ChildItem $localPath | Format-Table Name, Length, LastWriteTime
} else {
    Write-Host "❌ 本地发布失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 发布完成！" -ForegroundColor Green
Write-Host ""
Write-Host "📋 下一步操作：" -ForegroundColor Yellow
Write-Host "1. 访问 https://jitpack.io" -ForegroundColor White
Write-Host "2. 输入仓库地址: $GitHubUsername/MPChartMarker" -ForegroundColor White
Write-Host "3. 选择标签: v$Version" -ForegroundColor White
Write-Host "4. 点击 'Get it' 等待构建完成" -ForegroundColor White
Write-Host ""
Write-Host "📚 JitPack 使用方式：" -ForegroundColor Cyan
Write-Host "repositories {" -ForegroundColor Gray
Write-Host "    maven { url = uri(`"https://jitpack.io`") }" -ForegroundColor Gray
Write-Host "}" -ForegroundColor Gray
Write-Host "dependencies {" -ForegroundColor Gray
Write-Host "    implementation(`"com.github.$GitHubUsername`:MPChartMarker:v$Version`")" -ForegroundColor White
Write-Host "}" -ForegroundColor Gray
Write-Host ""
Write-Host "🔗 本地测试：" -ForegroundColor DarkCyan
Write-Host "repositories {" -ForegroundColor Gray
Write-Host "    mavenLocal()" -ForegroundColor Gray
Write-Host "}" -ForegroundColor Gray
Write-Host "dependencies {" -ForegroundColor Gray
Write-Host "    implementation(`"com.alex.klinemarker:kline-marker-lib:$Version`")" -ForegroundColor White
Write-Host "}" -ForegroundColor Gray
Write-Host ""
Write-Host "⚠️  重要提醒：" -ForegroundColor Red
Write-Host "请确保修改 kline-marker-lib/build.gradle.kts 中的以下信息："
Write-Host "- url.set(`"https://github.com/$GitHubUsername/MPChartMarker`")"
Write-Host "- developer id, name, email"
Write-Host "- scm 相关的 GitHub 地址" 