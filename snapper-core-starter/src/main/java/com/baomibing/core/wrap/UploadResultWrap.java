/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true)
/**
 * 文件上传结果
 * 
 * @author zening
 * @since 1.0.0
 */
public class UploadResultWrap implements Serializable {
	private static final long serialVersionUID = -8580262635791727217L;

	private String uid;
	private String name;
	private String status;
	//for delete the file
	private String groupName;
	private String remotePath;
	private String url;
	private String extName;
	
}
