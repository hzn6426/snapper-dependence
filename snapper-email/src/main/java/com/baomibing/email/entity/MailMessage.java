/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.email.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter @Setter
public class MailMessage {

	private String subject;
	
	private String from;
	
	private String to;
	
	private String cc;
	
	private String sendDate;
	
	private String content;
	
	private int messageNumber; //记录消息编号，以便失败后重新处理 
	
	private List<MaillAttchment> attchments = new ArrayList<>();
	
}
