/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.bo;

import jodd.mail.EmailAddress;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.util.List;
/**
 * 发送到供应商邮件
 * @author zening
 * @date 2018年8月27日 上午10:08:29
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class EmailBO  implements Serializable{
	private static final long serialVersionUID = 1L;
	private EmailAddress from;
	private String to;
	private String cc;//多个以;间隔
	private String subject;
	private String message;
	private String clientId;
	private List<File> attchments;
}
