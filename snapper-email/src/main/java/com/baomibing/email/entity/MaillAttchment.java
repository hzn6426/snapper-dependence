/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.entity;


import lombok.Getter;
import lombok.Setter;

import javax.mail.Part;
@Getter
@Setter
public class MaillAttchment {

	private String fileName;
	
	private String fileExtName;//扩展文件名
	
	private String fileSize;
	
	private Part filePart;
}
