package com.alex.klinemarker.utils;

import android.graphics.Paint;

import com.alex.klinemarker.data.MarkerShape;

/**
 * 文字处理工具类
 * 提供文字长度限制和居中计算等功能
 */
public class TextUtils {

    /**
     * 处理标记文字，限制长度（TEXT ONLY除外）
     *
     * @param text  原始文字
     * @param shape 标记形状
     * @return 处理后的文字
     */
    public static String processMarkerText(String text, MarkerShape shape) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // TEXT ONLY（NONE形状）不限制文字长度
        if (shape == MarkerShape.NONE) {
            return text;
        }

        // 检查是否包含中文字符
        boolean containsChinese = containsChineseCharacters(text);

        // 只有圆形和菱形支持中文字符显示
        if (containsChinese && !isChineseSupportedShape(shape)) {
            // 如果包含中文但形状不支持，则不显示文字
            return "";
        }

        // 其他形状限制为最多一个字符
        return getFirstCharacter(text);
    }

    /**
     * 检查字符串是否包含中文字符
     *
     * @param text 输入文字
     * @return 是否包含中文字符
     */
    public static boolean containsChineseCharacters(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);

            // 判断是否为汉字字符
            if (isChineseCharacter(codePoint)) {
                return true;
            }

            i += Character.charCount(codePoint);
        }

        return false;
    }

    /**
     * 检查形状是否支持中文字符显示
     *
     * @param shape 标记形状
     * @return 是否支持中文字符
     */
    public static boolean isChineseSupportedShape(MarkerShape shape) {
        return shape == MarkerShape.CIRCLE || shape == MarkerShape.DIAMOND;
    }

    /**
     * 获取字符串的第一个字符（支持汉字、英文、数字、符号）
     *
     * @param text 输入文字
     * @return 第一个字符
     */
    private static String getFirstCharacter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 使用 codePointAt 和 charCount 来正确处理 Unicode 字符
        // 这样可以正确处理各种语言的字符，包括 emoji 等
        int firstCodePoint = text.codePointAt(0);
        int charCount = Character.charCount(firstCodePoint);

        return text.substring(0, charCount);
    }

    /**
     * 计算文字的精确垂直居中位置（专门优化汉字显示）
     *
     * @param paint   文字画笔
     * @param centerY 标记中心Y坐标
     * @return 文字基线Y坐标
     */
    public static float calculateTextBaselineY(Paint paint, float centerY) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        // 使用更精确的垂直居中算法
        // fontMetrics.ascent是负值，descent是正值
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        // 计算基线位置，使文字在视觉上居中
        // 对于汉字，这种计算方式可以更好地处理字符的视觉中心
        return centerY - (fontMetrics.ascent + fontMetrics.descent) / 2;
    }

    /**
     * 计算文字的精确垂直居中位置（使用文字边界框）
     * 这个方法对于不同字符类型都有更好的居中效果
     *
     * @param paint   文字画笔
     * @param text    文字内容
     * @param centerY 标记中心Y坐标
     * @return 文字基线Y坐标
     */
    public static float calculateTextBaselineYWithBounds(Paint paint, String text, float centerY) {
        if (text == null || text.isEmpty()) {
            return centerY;
        }

        android.graphics.Rect textBounds = new android.graphics.Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        // 使用文字边界框的实际高度来计算居中位置
        // textBounds.exactCenterY() 可以提供更精确的视觉中心
        float textCenterY = textBounds.exactCenterY();

        // 调整基线位置，使文字的视觉中心与标记中心对齐
        return centerY - textCenterY;
    }

    /**
     * 判断字符串是否主要包含汉字
     *
     * @param text 输入文字
     * @return 是否主要是汉字
     */
    public static boolean isChineseText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        int chineseCharCount = 0;
        int totalCharCount = 0;

        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            totalCharCount++;

            // 判断是否为汉字字符
            if (isChineseCharacter(codePoint)) {
                chineseCharCount++;
            }

            i += Character.charCount(codePoint);
        }

        // 如果汉字字符占50%以上，认为是汉字文本
        return totalCharCount > 0 && (chineseCharCount * 2 >= totalCharCount);
    }

    /**
     * 判断单个字符是否为汉字
     *
     * @param codePoint 字符码点
     * @return 是否为汉字
     */
    private static boolean isChineseCharacter(int codePoint) {
        // CJK统一汉字基本区
        if (codePoint >= 0x4E00 && codePoint <= 0x9FFF) {
            return true;
        }

        // CJK统一汉字扩展A区
        if (codePoint >= 0x3400 && codePoint <= 0x4DBF) {
            return true;
        }

        // CJK统一汉字扩展B区
        if (codePoint >= 0x20000 && codePoint <= 0x2A6DF) {
            return true;
        }

        // CJK兼容汉字
        return codePoint >= 0xF900 && codePoint <= 0xFAFF;
    }

    /**
     * 为汉字优化的居中计算（使用字体度量）
     *
     * @param paint   文字画笔
     * @param text    文字内容
     * @param centerY 标记中心Y坐标
     * @return 文字基线Y坐标
     */
    public static float calculateChineseTextBaselineY(Paint paint, String text, float centerY) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        // 对于汉字，使用字体的CapHeight来计算，通常能得到更好的视觉效果
        // 汉字的视觉中心通常比西文字符更靠上一些

        if (isChineseText(text)) {
            // 汉字优化：使用稍微偏上的基线位置
            float visualCenter = (fontMetrics.ascent + fontMetrics.descent) / 2;
            return centerY - visualCenter * 0.9f; // 稍微向上调整10%
        } else {
            // 非汉字使用标准计算
            return calculateTextBaselineY(paint, centerY);
        }
    }
} 