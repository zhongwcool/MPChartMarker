package com.alex.klinemarker.data;

/**
 * 预定义的标记配置
 * 提供常用的标记样式，方便快速使用
 */
public class MarkerPresets {

    /**
     * 买入标记 - 深绿色向上三角形（提高对比度）
     */
    public static MarkerConfig buy() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.TRIANGLE_UP)
                .position(MarkerPosition.BELOW)
                .backgroundColor(0xFF2E7D32)  // 改为深绿色，提高对比度
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 卖出标记 - 红色向下三角形
     */
    public static MarkerConfig sell() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.TRIANGLE_DOWN)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFFF44336)  // 红色
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 警告标记 - 深橙色圆形带感叹号（改为更醒目的深橙色）
     */
    public static MarkerConfig warning() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFFE65100)  // 改为深橙色，更醒目
                .textColor(0xFFFFFFFF)
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 信息标记 - 蓝色圆形
     */
    public static MarkerConfig info() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.AUTO)
                .backgroundColor(0xFF1976D2)  // 改为深蓝色，提高对比度
                .textColor(0xFFFFFFFF)
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 成功标记 - 深绿色圆形带勾
     */
    public static MarkerConfig success() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFF2E7D32)  // 改为深绿色，提高对比度
                .textColor(0xFFFFFFFF)
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 错误标记 - 红色圆形带叉
     */
    public static MarkerConfig error() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CIRCLE)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFFF44336)  // 红色
                .textColor(0xFFFFFFFF)
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .build();
    }

    /**
     * 事件标记 - 深紫色菱形（提高对比度）
     */
    public static MarkerConfig event() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.DIAMOND)
                .position(MarkerPosition.AUTO)
                .backgroundColor(0xFF6A1B9A)  // 改为深紫色，提高对比度
                .textColor(0xFFFFFFFF)
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.LONG)
                .build();
    }

    /**
     * 止损标记 - 深红色向下箭头
     */
    public static MarkerConfig stopLoss() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.ARROW_DOWN)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFFB71C1C)  // 深红色
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .dashedLine(false)  // 实线
                .build();
    }

    /**
     * 止盈标记 - 深绿色向上箭头
     */
    public static MarkerConfig takeProfit() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.ARROW_UP)
                .position(MarkerPosition.BELOW)
                .backgroundColor(0xFF1B5E20)  // 深绿色
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.SHORT)
                .dashedLine(false)  // 实线
                .build();
    }

    /**
     * 纯文本标记 - 无形状，仅显示文字
     * 文字从指示线末端开始显示，使用较小的加粗字体
     */
    public static MarkerConfig textOnly() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.NONE)
                .position(MarkerPosition.AUTO)
                .showText(true)
                .showLine(true)
                .textColor(0xFF333333)
                .lineColor(0xFF666666)
                .textSize(10f)    // 降低文字尺寸 (从12f改为10f)
                .lineLength(LineLength.MEDIUM)
                .build();
    }

    /**
     * 重要标记 - 金色五角星（原黄色改为更醒目的金色）
     */
    public static MarkerConfig important() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.STAR)
                .position(MarkerPosition.ABOVE)
                .backgroundColor(0xFFFF8F00)  // 改为金色，更加醒目易识别
                .showText(true)
                .showLine(true)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .textSize(8f)     // 调整文字尺寸 (从6f改为8f)
                .lineLength(LineLength.MEDIUM)
                .zIndex(10)  // 高优先级显示
                .build();
    }

    /**
     * 简单点标记 - 小圆点，显示虚线连接
     */
    public static MarkerConfig dot() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.DOT)
                .position(MarkerPosition.AUTO)
                .backgroundColor(0xFF666666)  // 灰色
                .showText(false)
                .showLine(false)               // 启用连接线显示
                .lineColor(0xFF999999)        // 设置连接线颜色
                .lineLength(LineLength.SHORT) // 设置连接线长度
                .markerSize(12f)              // 调整尺寸 (从4f改为12f)
                .build();
    }

    /**
     * 十字标记 - 用于精确定位
     */
    public static MarkerConfig cross() {
        return new MarkerConfig.Builder()
                .shape(MarkerShape.CROSS)
                .position(MarkerPosition.AUTO)
                .backgroundColor(0xFF000000)  // 黑色
                .showText(false)
                .showLine(false)
                .markerSize(12f)  // 调整尺寸 (从8f改为12f)
                .lineWidth(2f)
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