/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.constant.enums;

import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 文件类型枚举
 * 
 * @author zening
 * @date Jun 8, 2021 2:50:43 PM
 * @version 1.0.0
 */
public enum FileTypeEnum {

	IMAGE, PPT, WORD, EXCEL, TXT, ZIP, PDF, FILE, FOLDER;

	private static Set<String> images = Sets.newHashSet("png", "jpeg", "jpg", "gif", "bmp");

	private static Set<String> zips = Sets.newHashSet("zip", "rar", "tar", "tar.gz");

	private static Set<String> words = Sets.newHashSet("doc", "docx");

	private static Set<String> excels = Sets.newHashSet("xls", "xlsx");

	private static Set<String> ppts = Sets.newHashSet("ppt", "pptx");

	public static FileTypeEnum getByExtName(String extName) {
		if (Checker.beEmpty(extName)) {
			return FOLDER;
		}
		if (images.contains(extName.toLowerCase())) {
			return IMAGE;
		} else if (zips.contains(extName.toLowerCase())) {
			return ZIP;
		} else if (words.contains(extName.toLowerCase())) {
			return WORD;
		} else if (excels.contains(extName.toLowerCase())) {
			return EXCEL;
		} else if (ppts.contains(extName.toLowerCase())) {
			return PPT;
		} else if (PDF.name().toLowerCase().equals(extName.toLowerCase())) {
			return PDF;
		} else if (TXT.name().toLowerCase().equals(extName.toLowerCase())) {
			return TXT;
		} else {
			return FILE;
		}
		

	}
}
