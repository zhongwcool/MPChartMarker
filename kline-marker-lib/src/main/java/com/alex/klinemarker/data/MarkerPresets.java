package com.alex.klinemarker.data;

/**
 * 预定义的标记配置
 * 提供常用的标记样式，方便快速使用
 * 现在使用统一的颜色预设系统，避免杂乱的颜色
 */
public class MarkerPresets {

    /**
     * 买入标记 - 使用谷歌绿色
     */
    public static MarkerConfig buy() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.RECTANGLE)
                .position(MarkerPosition.BELOW)
                .backgroundColor(MarkerColors.BuyColors.BACKGROUND)
                .textColor(MarkerColors.BuyColors.TEXT)
                .lineColor(MarkerColors.BuyColors.LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 卖出标记 - 使用谷歌红色
     */
    public static MarkerConfig sell() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.RECTANGLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.SellColors.BACKGROUND)
                .textColor(MarkerColors.SellColors.TEXT)
                .lineColor(MarkerColors.SellColors.LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 警告标记 - 使用谷歌红色（警告用红色更醒目）
     */
    public static MarkerConfig warning() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.GOOGLE_RED)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_RED))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 信息标记 - 使用谷歌蓝色
     */
    public static MarkerConfig info() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.InfoColors.BACKGROUND)
                .textColor(MarkerColors.InfoColors.TEXT)
                .lineColor(MarkerColors.InfoColors.LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 成功标记 - 使用谷歌绿色
     */
    public static MarkerConfig success() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.GOOGLE_GREEN)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_GREEN))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 错误标记 - 使用谷歌红色
     */
    public static MarkerConfig error() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.GOOGLE_RED)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_RED))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 事件标记 - 使用经典菱形紫色
     */
    public static MarkerConfig event() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.DIAMOND)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.EventColors.BACKGROUND)
                .textColor(MarkerColors.EventColors.TEXT)
                .lineColor(MarkerColors.EventColors.LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.LONG)
                .build();
    }

    /**
     * 止损标记 - 使用股票红色
     */
    public static MarkerConfig stopLoss() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.ARROW_DOWN)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.StockColors.DOWN_BACKGROUND)
                .textColor(MarkerColors.StockColors.TEXT)
                .lineColor(MarkerColors.StockColors.DOWN_LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .dashedLine(false)  // 实线
                .build();
    }

    /**
     * 止盈标记 - 使用股票绿色
     */
    public static MarkerConfig takeProfit() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.ARROW_UP)
                .position(MarkerPosition.BELOW)
                .backgroundColor(MarkerColors.StockColors.UP_BACKGROUND)
                .textColor(MarkerColors.StockColors.TEXT)
                .lineColor(MarkerColors.StockColors.UP_LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .dashedLine(false)  // 实线
                .build();
    }

    /**
     * 纯文本标记 - 使用中性灰色
     */
    public static MarkerConfig textOnly() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.NONE)
                .position(MarkerPosition.AUTO)
                .showText(true)
                .showLine(true)
                .textColor(0xFF333333)
                .lineColor(0xFF666666)
                .textSize(12f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 重要标记 - 使用谷歌黄色五角星
     */
    public static MarkerConfig important() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.STAR)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.ImportantColors.BACKGROUND)
                .textColor(MarkerColors.ImportantColors.TEXT)
                .lineColor(MarkerColors.ImportantColors.LINE)
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .zIndex(10)  // 高优先级显示
                .build();
    }

    /**
     * 创建圆点标记（无文字）
     */
    public static MarkerConfig dot() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.DOT)
                .backgroundColor(0xFF666666) // 中性灰色
                .showText(false)
                .showLine(true)
                .lineColor(0xFF999999)
                .lineLength(LineLength.SHORT)
                .markerSize(4f) // 圆点通常比较小
                .build();
    }

    // ========== 基于颜色预设的新增方法 ==========

    /**
     * 创建使用谷歌蓝色的标记
     */
    public static MarkerConfig googleBlue() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.GOOGLE_BLUE)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_BLUE))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 创建使用谷歌红色的标记
     */
    public static MarkerConfig googleRed() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.GOOGLE_RED)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_RED))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 创建使用谷歌绿色的标记
     */
    public static MarkerConfig googleGreen() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.GOOGLE_GREEN)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_GREEN))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 创建使用谷歌黄色的标记
     */
    public static MarkerConfig googleYellow() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.GOOGLE_YELLOW)
                .textColor(0xFF000000)  // 黄色背景用黑色文字
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.GOOGLE_YELLOW))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 创建使用股票绿色的标记（中国股市上涨色）
     */
    public static MarkerConfig stockGreen() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.TRIANGLE_UP)
                .position(MarkerPosition.BELOW)
                .backgroundColor(MarkerColors.STOCK_GREEN)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.STOCK_GREEN))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 创建使用股票红色的标记（中国股市下跌色）
     */
    public static MarkerConfig stockRed() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.TRIANGLE_DOWN)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(MarkerColors.STOCK_RED)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.STOCK_RED))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 创建使用菱形紫色的标记
     */
    public static MarkerConfig diamondPurple() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.DIAMOND)
                .position(MarkerPosition.AUTO)
                .backgroundColor(MarkerColors.DIAMOND_PURPLE)
                .textColor(0xFFFFFFFF)
                .lineColor(MarkerColors.getDarkerVariant(MarkerColors.DIAMOND_PURPLE))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 根据颜色索引创建预设标记
     * @param colorIndex 主要调色板中的颜色索引（0-6）
     * @param shape 标记形状
     * @return 标记配置
     */
    public static MarkerConfig fromColorIndex(int colorIndex, MarkerShape shape) {
        int backgroundColor = MarkerColors.getPrimaryColor(colorIndex);
        int textColor = (backgroundColor == MarkerColors.GOOGLE_YELLOW) ? 0xFF000000 : 0xFFFFFFFF;
        
        return new MarkerConfig.Builder()
                .shape(shape)
                .position(MarkerPosition.AUTO)
                .backgroundColor(backgroundColor)
                .textColor(textColor)
                .lineColor(MarkerColors.getDarkerVariant(backgroundColor))
                .showText(true)
                .showLine(true)
                .markerSize(16f)
                .textSize(10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 自定义样式的便捷方法
     *
     * @param baseConfig 基础配置
     * @return 新的构建器，可以继续自定义
     */
    public static MarkerConfig.Builder customize(MarkerConfig baseConfig) {
        MarkerConfig copy = baseConfig.copy();
        return new MarkerConfig.Builder()
                .shape(copy.getShape())
                .position(copy.getPosition())
                .showText(copy.isShowText())
                .showLine(copy.isShowLine())
                .backgroundColor(copy.getBackgroundColor())
                .textColor(copy.getTextColor())
                .lineColor(copy.getLineColor())
                .markerSize(copy.getMarkerSize())
                .textSize(copy.getTextSize())
                .lineWidth(copy.getLineWidth())
                .lineLength(copy.getLineLength())
                .dashedLine(copy.isDashedLine())
                .dashPattern(copy.getDashPattern())
                .alpha(copy.getAlpha())
                .zIndex(copy.getZIndex());
    }
} 