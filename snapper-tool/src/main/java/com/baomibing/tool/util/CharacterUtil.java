/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.tool.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符工具
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public abstract class CharacterUtil {

	/**
	 * 字符串仅保留数字
	 * 
	 * @param s 字符串
	 * @return 操作后的字符串
	 */
	public static String justLeftNumbers(String s) {
		if (Checker.beEmpty(s))
			return s;
		String regex = "[^0-9.]";
		String target = s.replaceAll(regex, "");
		if (Checker.beEmpty(s))
			return "0";
		return target;
	}

	/**
	 * 字符串仅保留字符串
	 * 
	 * @param s 字符串
	 * @return 操作后的字符串
	 */
	public static String justLeftStrings(String s) {
		if (Checker.beEmpty(s))
			return s;
		String regex = "[^a-zA-Z\\u4E00-\\u9FA5]";
		return s.replaceAll(regex, "");
	}

	/**
	 * 字符串仅保留字符串和数字
	 * 
	 * @param s 字符串
	 * @return 操作后的字符串
	 */
	public static String justLeftStringAndNumbers(String s) {
		if (Checker.beEmpty(s))
			return s;
		String regex = "[^a-zA-Z0-9.\\u4E00-\\u9FA5]";
		return s.replaceAll(regex, "");
	}

	/**
	 * 字符串中仅保留数字和/
	 * 
	 * @param s 字符串
	 * @return 操作后的字符串
	 */
	public static String justLeftIntAndSlash(String s) {
		if (Checker.beEmpty(s))
			return s;
		String regex = "[^0-9/]";
		return s.replaceAll(regex, "");
	}

	public static String justLeftNotStringAndNumbers(String s) {
		if (Checker.beEmpty(s)) return s;
		StringBuilder sbuilder = new StringBuilder();
		StringBuffer sb = new StringBuffer(s);
		int l = sb.length();

		for(int i = 0; i < l; ++i) {
			char c = sb.charAt(i);
			boolean validChars = ArrayUtil.containsIgnoreCase(numbersAndChars, c);
			if (!validChars) {
				sbuilder.append(c);
			}
		}
		return sbuilder.toString();
	}

	/**
	 * 查询字符串出现的次数
	 * 
	 * @param str    原始字符
	 * @param subStr 查找的字符串
	 * @param n      查询起始索引
	 * @return 操作后的字符串
	 */
	public static int searchIndexOfNth(String str, String subStr, int n) {
		return StringUtils.ordinalIndexOf(str, subStr, n);
	}

	/**
	 * 获取中文字符的简拼音(每个汉字拼音首字母)小写,英文原样输出
	 * 
	 * @param chinese 汉字
	 * @return 操作后的字符串
	 */
	public static String getShortPinYin(String chinese) {
		String pinyinName = "";
		char[] nameChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		try {
			for (int i = 0; i < nameChar.length; ++i) {
				if (nameChar[i] > 128 && nameChar[i] < 65000) {
					String [] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					String pinyin = "";
					if (Checker.beNotEmpty(pinyinArray)) {
						pinyin = "" + pinyinArray[0].charAt(0);
					}
					pinyinName = pinyinName + pinyin;
				} else {
					pinyinName = pinyinName + nameChar[i];
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination bpof) {
			log.error("cannot generate pinyin, exception is: {}", Throwables.getStackTraceAsString(bpof));
		}
		return pinyinName;
	}

	/**
	 * 获取中文字符的全拼音小写,英文原样输出
	 * 
	 * @param chinese 汉字
	 * @return 操作后的字符串
	 */
	public static String getFullPinYin(String chinese) {
		String pinyinName = "";
		char[] nameChar = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		try {
			for (int i = 0; i < nameChar.length; ++i) {
				if (nameChar[i] > 128 && nameChar[i] < 65000) {
					String [] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
					String pinyin = "";
					if (Checker.beNotEmpty(pinyinArray)) {
						pinyin = "" + pinyinArray[0];
					}
					pinyinName = pinyinName + pinyin;
				} else {
					pinyinName = pinyinName + nameChar[i];
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination bpof) {
			log.error("cannot generate pinyin, exception is: {}", Throwables.getStackTraceAsString(bpof));
		}

		return pinyinName;
	}

	/**
	 * 对比两个字符串，标红差异
	 * 
	 * @param old    旧字符串
	 * @param latest 新字符串
	 * @return 操作后的字符串
	 */
	public static String compareHtmlRedDifference(String old, String latest) {
		String s = compareDifferencePrettyHTML(old, latest);
		return removeContentsOfHtmlTags(s, "del");
	}

	public static String compareDifferencePrettyHTML(String old, String latest) {
		if (Checker.beEmpty(latest))
			return "";
		if (Checker.beEmpty(old)) {
			return latest;
		}
		DiffMatchPatch dmp = new DiffMatchPatch();
		LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(old, latest);
		dmp.diffCleanupSemantic(diffs);
		String target = dmp.diffPrettyHtml(diffs);
		return target.replaceAll("&para;", "");
	}

	public static List<String> compareDifferences(String old, String latest) {
		if (Checker.beEmpty(latest))
			return Lists.newArrayList();
		List<String> list = new ArrayList<String>();
		if (Checker.beEmpty(old)) {
			list.add(latest);
			return list;
		}
		DiffMatchPatch dmp = new DiffMatchPatch();
		LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(old, latest);
		dmp.diffCleanupSemantic(diffs);
		diffs.forEach(d -> {
			if (d.operation.equals(DiffMatchPatch.Operation.INSERT)) {
				list.add(d.text);
			}
		});
		return list;
	}

	/**
	 * 返回字符的大写字符
	 * 
	 * @param s
	 * @return 操作后的字符串
	 */
	public static String toUpperCase(String s) {
		if (Checker.beNotEmpty(s))
			return s.toUpperCase();
		return s;
	}

	private static final char[] chars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private static final char[] numbersAndChars = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
	/**
	 * 获取字母的下一个字母
	 * 
	 * @param c 当前字母
	 * @return 下一个字符
	 */
	public static char nextChar(char c) {
		int index = 0;
		for (int i = 0, length = chars.length; i < length; i++) {
			if (CharUtils.compare(chars[i], Character.toUpperCase(c)) == 0) {
				index = i;
				break;
			}
		}
		if (index > chars.length - 1) {
			return 'A';
		}
		return chars[index + 1];
	}

	/**
	 * 用字符替换回车，换行，制表
	 * 
	 * @param s               要处理的字符串
	 * @param replaceChars    要替换成的字符
	 * @param ignoreEmptyLine 是否忽略空行
	 * @return 操作后的字符串
	 */
	public static String replaceLineBreak(String s, String replaceChars, boolean ignoreEmptyLine) {
		if (Checker.beEmpty(s))
			return s;
		replaceChars = Checker.beEmpty(replaceChars) ? "$_$" : replaceChars;
		s = s.replaceAll("[\\t\\n\\r]", replaceChars);
		List<String> splitList = Lists.newArrayList();
		if (ignoreEmptyLine) {
			splitList = Splitter.on(replaceChars).omitEmptyStrings().splitToList(s);
		} else {
			splitList = Splitter.on(replaceChars).splitToList(s);
		}
		return Joiner.on(replaceChars).join(splitList);
	}

	/**
	 * 替换旧字符为新字符
	 * 
	 * @param s        字符串
	 * @param oldChars 旧字符
	 * @param newChars 新字符
	 * @return 操作后的字符串
	 */
	public static String replaceAll(String s, String oldChars, String newChars) {
		if (Checker.beEmpty(s))
			return s;
		s = s.replaceAll("[\\t\\n\\r]", "");
		return s.replaceAll(oldChars, newChars);
	}

	/**
	 * 判断字符是不是数字
	 * 
	 * @param s 字符串
	 * @return 是否数字
	 */
	public static boolean isNumeric(String s) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(s).matches();
	}

	/**
	 * 查找字符个数，右侧补齐用某个字符补齐足够个数
	 * 
	 * @param s           字符串
	 * @param charsNumber 字符的个数
	 * @param chars       某个字符
	 * @return 操作后的字符串
	 */
	public static String rightPadSomeChars(String s, int charsNumber, String chars) {
		if (Checker.beEmpty(s))
			return s;
		if (Checker.beEmpty(chars)) {
			chars = " ";
		}
		int slength = s.length();
		int charsLength = chars.length();
		int hasNumber = StringUtils.countMatches(s, chars);
		int needNumber = charsNumber - hasNumber;
		if (needNumber < 1)
			return s;
		int needLength = needNumber * charsLength;
		return StringUtils.rightPad(s, slength + needLength, chars);
	}

	/**
	 * 查找字符个数，左侧补齐用某个字符补齐足够个数
	 * 
	 * @param s           字符串
	 * @param charsNumber 字符的个数
	 * @param chars       某个字符
	 * @return 操作后的字符串
	 */
	public static String leftPadSomeChars(String s, int charsNumber, String chars) {
		if (Checker.beEmpty(s))
			return s;
		if (Checker.beEmpty(chars)) {
			return s;
		}
		return StringUtils.leftPad(s, charsNumber, chars);
	}

	/**
	 * 在字符串每间隔index处插入字符 chars
	 * 
	 * @param s     操作的字符串
	 * @param index 间隔大小
	 * @param chars 带插入的字符串
	 * @return 操作后的字符串
	 */
	public static String insertCharAtIntervalIndex(String s, int index, String chars) {
		if (Checker.beEmpty(s) || s.length() < index + 1)
			return s;
		String withDashes = s.substring(0, index);
		int i = index;
		while ((i + index) < s.length()) {
			withDashes += chars + s.substring(i, i + index);
			i = i + index;
		}

		return withDashes;
	}

	public static boolean beContainsChineseChars(String s) {
		if (Checker.beEmpty(s)) {
			return false;
		} else {
			StringBuffer sb = new StringBuffer(s);
			int l = sb.length();

			for(int i = 0; i < l; ++i) {
				char c = sb.charAt(i);
				boolean validChars = c > 31 && c < 127 || c == '\r' || c == '\n';
				if (!validChars) {
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * 除去字符串html标签中的内容
	 * 
	 * @param s
	 * @param tag
	 * @return 操作后的字符串
	 */
	public static String removeContentsOfHtmlTags(String s, String tag) {
		if (Checker.beEmpty(s))
			return s;
		String regex = "<" + tag + "[^>]*?>[\\s\\S]*?</" + tag + ">";
		return s.replaceAll(regex, "");
	}

	public static String findIndexContainsStrings(String s, String searcher) {
		if (Checker.beEmpty(s) || Checker.beEmpty(searcher)) return s;
		if (!s.contains(searcher)) return s;
		return s.substring(0, s.indexOf(searcher) + searcher.length());
	}
}
