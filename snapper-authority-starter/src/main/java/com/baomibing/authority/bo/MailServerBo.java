/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.bo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MailServerBo {

	private String host;
	
	private String address;
	
	private String passwd;

	private String protocol;
	
	private String fromTitle;

	private String titlePrefix;

	private String websiteUrl;
}
