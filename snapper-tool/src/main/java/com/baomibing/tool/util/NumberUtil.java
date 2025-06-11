/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.util;

import com.ibm.icu.text.RuleBasedNumberFormat;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

import static com.baomibing.tool.util.ConvertUtil.toBigDecimal;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * 数字工具类
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class NumberUtil {

	/**
	 * 获得除法结果one/two,进行四舍五入保留scale位小数
	 * 
	 * @param one   第一个数字
	 * @param two   第二个数字
	 * @param scale 小数位数
	 * @return 相除结果
	 */
	public static BigDecimal getDivideValue(Number one, Number two, int scale) {
		return getDivideValue(one, two, scale, HALF_UP);
	}

	/**
	 * 获得除法结果one/two,指定舍入方式 roundingMode 以及 小数位数 scale
	 * 
	 * <pre class="code">
	 * NumberUtil.getDivideValue(0, 2, 0,RoundingMode.HALF_UP)   =   0
	 * NumberUtil.getDivideValue(6, 4, 0,RoundingMode.HALF_UP)   =   2
	 * NumberUtil.getDivideValue(10, 3, 2,RoundingMode.HALF_UP)  =   3.33
	 * NumberUtil.getDivideValue(5, 3, 2,RoundingMode.HALF_UP)   =   1.67
	 * </pre>
	 * 
	 * @param one          第一个数字
	 * @param two          第二个数字
	 * @param scale        小数位数
	 * @param roundingMode 凑整方式
	 * @return 相除结果
	 */
	public static BigDecimal getDivideValue(Number one, Number two, int scale, RoundingMode roundingMode) {
		Validate.notNull(one, "one can't be null!");
		Validate.notNull(two, "two can't be null!");

		BigDecimal divisor = ConvertUtil.toBigDecimal(two);
		Validate.isTrue(!divisor.equals(ZERO), "two can't be zero!");

		// 不能直接one.divide(two),应该指定scale和roundingMode,保证对于无限小数有足够的范围来表示结果.
		// 避免 exception:Non-terminating decimal expansion; no exact representable
		// decimal result (无法结束的除法表达式；没有精确的除结果)
		return ConvertUtil.toBigDecimal(one).divide(divisor, scale, roundingMode);
	}

	/**
	 * 数字相乘，并根据四舍五入保留小数点<code>scale</code>位数
	 * 
	 * @param one   第一个数字
	 * @param two   第二个数字
	 * @param scale 小数位数
	 * @return 相乘结果
	 */
	public static BigDecimal getMultiplyValue(Number one, Number two, int scale) {
		Validate.notNull(one, "one can't be null!");
		Validate.notNull(two, "two can't be null!");
		// 默认返回的精度: (this.scale() + multiplicand.scale()).
		BigDecimal multiplyValue = toBigDecimal(one).multiply(toBigDecimal(two));
		return setScale(multiplyValue, scale);
	}

	/**
	 * 将数组中数字相加
	 * 
	 * @param numbers 数字数组
	 * @return 相加结果
	 */
	public static BigDecimal getSumValue(Number... numbers) {
		Validate.notEmpty(numbers, "numbers can't be null/empty!");
		Validate.noNullElements(numbers, "numbers can't has null element!");

		BigDecimal sum = ZERO;
		for (Number number : numbers) {
			sum = sum.add(toBigDecimal(number));
		}
		return sum;
	}

	/**
	 * 所有数相减
	 * 
	 * <pre class="code">
	 * NumberUtil.getSubtractValue(0, 2, 3)                 =   -5
	 * NumberUtil.getSubtractValue(0,null)                  =   0
	 * NumberUtil.getSubtractValue(0,  new Integer[5])      =   0
	 * NumberUtil.getSubtractValue(2, 1.1)                  =   0.9
	 * NumberUtil.getSubtractValue(1000, 99.5, 99.0)        =   801.5
	 * NumberUtil.getSubtractValue(1000, 50, null)          =   950
	 * NumberUtil.getSubtractValue(-1000, -50, 100)         =   -1050
	 * 
	 * NumberUtil.getSubtractValue(null, 5) // NullPointerException
	 * </pre>
	 * 
	 * @param beSubtractedValue 被减数
	 * @param subtractions      减数数组
	 * @return 返回相减的结果
	 */
	public static BigDecimal getSubtractValue(Number beSubtractedValue, Number... subtractions) {
		Validate.notNull(beSubtractedValue, "beSubtractedValue can't be null/empty!");

		BigDecimal result = toBigDecimal(beSubtractedValue);
		if (Checker.beEmpty(subtractions)) {
			return result;
		}

		for (Number subtraction : subtractions) {
			if (null == subtraction) {// 跳过 null 元素
				continue;
			}
			result = result.subtract(toBigDecimal(subtraction));
		}
		return result;
	}

	/**
	 * 将数字按照<code>numberPattern</code>四舍五入转化成字符串
	 * 
	 * @param value         数字
	 * @param numberPattern 格式
	 * @return 转化后的字符串
	 */
	public static String toString(Number value, String numberPattern) {
		return toString(value, numberPattern, HALF_UP);
	}

	/**
	 * 使用<code>numberPattern</code>对数字<code>number</code>按照<code>roundingMode</code>方式进行格式化
	 * 
	 * <pre class="code">
	 * NumberUtil.format(toBigDecimal(1.15), "#####.#",null)     =   1.2
	 * NumberUtil.format(toBigDecimal(1.25), "#####.#",null)     =   1.3
	 * NumberUtil.format(toBigDecimal(1.251), "#####.#",null)    =   1.3
	 * 
	 * NumberUtil.format(toBigDecimal(-1.15), "#####.#",null)    =   -1.2
	 * NumberUtil.format(toBigDecimal(-1.25), "#####.#",null)    =   -1.3
	 * NumberUtil.format(toBigDecimal(-1.251), "#####.#",null)   =   -1.3
	 * 
	 * 
	 * NumberUtil.format(toBigDecimal(1.15), "#####.#", RoundingMode.HALF_EVEN)     =   1.2
	 * NumberUtil.format(toBigDecimal(1.25), "#####.#", RoundingMode.HALF_EVEN)     =   1.2
	 * NumberUtil.format(toBigDecimal(1.251), "#####.#", RoundingMode.HALF_EVEN)    =   1.3
	 * 
	 * NumberUtil.format(toBigDecimal(-1.15), "#####.#", RoundingMode.HALF_EVEN)    =   -1.2
	 * NumberUtil.format(toBigDecimal(-1.25), "#####.#", RoundingMode.HALF_EVEN)    =   -1.2
	 * NumberUtil.format(toBigDecimal(-1.251), "#####.#", RoundingMode.HALF_EVEN)   =   -1.3
	 * </pre>
	 * 
	 * @param value         数字
	 * @param numberPattern 数字格式
	 * @param roundingMode  凑整方式
	 * @return 格式化后的结果
	 */
	public static String toString(Number value, String numberPattern, RoundingMode roundingMode) {
		Validate.notNull(value, "value can't be null!");
		Validate.notBlank(numberPattern, "numberPattern can't be null!");

		// 该构造方法内部 调用了applyPattern(pattern, false)
		DecimalFormat decimalFormat = new DecimalFormat(numberPattern);
		decimalFormat.setRoundingMode(defaultIfNull(roundingMode, HALF_UP));

		String result = decimalFormat.format(value);

		return result;
	}

	/**
	 * 计算进度(当前量 <code>current</code>/总量 <code>total</code>,然后转成指定的字符串格式
	 * <code>numberPattern</code>). NumberUtil.getProgress(5, 5, "##%") = "100%"
	 * NumberUtil.getProgress(2, 3, "#0.0%") = "66.7%" NumberUtil.getProgress(2, 3,
	 * "#0.00%") = "66.67%"
	 * 
	 * @param current       当前进度数字
	 * @param total         总数数字
	 * @param numberPattern 格式
	 * @return 进度的百分比字符串
	 */
	public static String getProgress(Number current, Number total, String numberPattern) {
		Validate.notNull(current, "current can't be null/empty!");
		Validate.notNull(total, "total can't be null/empty!");

		Validate.isTrue(current.intValue() > 0, "current can not <=0");
		Validate.isTrue(total.intValue() > 0, "total can not <=0");
		Validate.isTrue(current.doubleValue() <= total.doubleValue(), "current can not > total");
		// 设置足够多的位数
		int scale = 8;
		BigDecimal divideValue = getDivideValue(current, total, scale);
		return toString(divideValue, numberPattern);
	}

	/**
	 * 使用四舍五入设置小数点位数
	 * 
	 * @param value 数字
	 * @param scale 小数点位数
	 * @return 四舍五入后的结果
	 */
	public static BigDecimal setScale(Number value, int scale) {
		return setScale(value, scale, HALF_UP);
	}

	/**
	 * 使用roundingMode 来设置小数点位数.
	 * 
	 * @param value        数字
	 * @param scale        小数点位数
	 * @param roundingMode 凑整方式
	 * @return 四舍五入后结果
	 */
	public static BigDecimal setScale(Number value, int scale, RoundingMode roundingMode) {
		Validate.notNull(value, "value can't be null!");
		return toBigDecimal(value).setScale(scale, defaultIfNull(roundingMode, HALF_UP));
	}

	/**
	 * 数字转英文大写字母
	 * 
	 * @param number
	 * @return 操作后的字符串
	 */
	public static String number2UpperCaseEnglish(Number number) {
		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
		return nf.format(number).toUpperCase();
	}
}
