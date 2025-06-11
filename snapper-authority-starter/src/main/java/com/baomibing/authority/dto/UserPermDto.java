
package com.baomibing.authority.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 前端用来封装分配权限数据的Dto
 * 
 * @author zening
 * @version  1.0.0
 */
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true)
public class UserPermDto {

	private List<String> menus;
	private List<String> buttons;
	private String userId;
	private String groupId;
	private String menuId;// 保存按钮权限时候对应的菜单ID
}
