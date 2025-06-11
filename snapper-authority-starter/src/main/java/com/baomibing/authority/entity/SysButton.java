
package com.baomibing.authority.entity;

import com.baomibing.orm.base.MBaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 系统按钮-所有用户公用一套系统按钮,只有开发管理员才能有修改权限
 * 
 * @author zening
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_button")
public class SysButton extends MBaseModel {
	private static final long serialVersionUID = -1072443857437901070L;

	@TableId(value = "id", type = IdType.INPUT)
    private String id;
    @TableField("button_name")
    private String buttonName;
    @TableField("menu_id")
    private String menuId;
    @TableField("note")
    private String note;
    @TableField("be_unauth")
    private Boolean beUnauth;
	@TableField("sub_menu")
	private String subMenu;
	private String groupId;
	private Boolean beLock;


	private transient String reqMethod;

	private transient String reqUrl;
	// 业务权限范围
	private transient String permScope;
	// 业务权限ID
	private transient String permId;
	// 权限时效开始时间
	private transient Date permStartTime;
	// 权限时效结束时间
	private transient Date permEndTime;
	/**
	 * 权限动作
	 */
	private transient String permAction;

	private transient String actionRef;

	private transient String dataPerm;

	private transient String columnPerm;



}
