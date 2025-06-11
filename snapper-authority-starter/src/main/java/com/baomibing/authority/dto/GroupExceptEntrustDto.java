
package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户组织委托
 * @author zening
 * @version 1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true) 
public class GroupExceptEntrustDto {

	private String id;
	private String userId;
	private String groupExceptEntrustId;
//	@ApiModelProperty("业务权限动作")
//	private String permAction;
	private String permId;
	// 用户所在组织
	private String orgId;
}
