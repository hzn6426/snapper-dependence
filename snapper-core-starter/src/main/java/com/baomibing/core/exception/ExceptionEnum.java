/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.exception;

import com.baomibing.tool.exception.ExceptionEnumable;

/**
 * 异常枚举
 * 
 * @author zening
 * @since 1.0.0
 */
public enum ExceptionEnum implements ExceptionEnumable{

	NO_PRIVILEGE_EXCEPTION(403, "用户没有访问资源的权限!"),
	USER_BE_UNAUTHERIZED(401, "用户权限资源认证失败，没有权限!"),
	CAPTCHA_NOT_CORRECT(995, "验证码错误, 请填写正确验证码!"),
	OBJECT_BE_LOCK_NOT_OPERATE(996, "该信息已被锁定，无法进行此操作！"),
	USER_NAME_NOT_EXIST(999, "用户名不存在!"),
	USER_NAME_OR_PASSWD_NOT_CORRECT(998,"用户名或密码错误!"),
	USER_NOT_ACTIVE(997, "用户未激活,请联系管理员进行激活!"),
	USER_ACCOUNT_STOPPED(996, "用户已停用, 请联系管理员!"),
	USER_ACCOUNT_LOCKED(995, "用户已锁定, 请联系管理员!"),
	USER_IP_LOCKED(994, "用户IP已锁定, 请联系管理员!"),
	USER_AUTH_CODE_EXPIRE(993, "用户授权码已过期, 请联系授权人进行续期!"),
	USER_NOT_OPEN_AUTH_CODE(992, "用户未设置授权码登录信息,不能在授权码登录页面进行登录!"),
	UN_CHECKED_EXCEPTION(10000, "内部错误，未知的系统运行异常！"),
	NO_BUNDLE_CODE(10001, "未绑定的异常代码！{0}"),
	OBJECT_IS_NULL(10002, "空指针异常{0}，无法进行后续操作！"),
	ILLEGAL_ARGUMENT(10003, "内部错误，不合法的参数异常！"),
	INVALID_ID_FOR_UPDATE(10004, "更新数据失败，非法的ID！"),
	INVALID_ID_FOR_INSERT(10005, "插入数据失败，非法的ID！"),
	INVALID_ID_FOR_DELETE(10006, "删除数据失败，非法的ID！"),
	INVALID_UPDATE_NUM(10007, "更新数据失败，影响行数大于1！"),
	INVALID_DELETE_NUM(10008, "删除数据失败，影响行数大于1！"),
	INVALID_PK_FOR_UPDATE(10009, "更新数据失败，非法的主键！"),
	INVALID_PK_FOR_DELETE(10010, "删除数据失败，非法的主键！"),
	INVALID_PK_FOR_GET(10011, "查询数据失败，非法的主键！"),
	FIELD_NOT_BE_EMPTY(10012, "存储失败，字段{0}不能为空!"),
	SQL_NOT_SELECT_STATEMENT(10013, "SQL错误，没有SELECT语句！"),
	RUN_TIME_EXCEPTION(10014, "运行时异常{0}！"),
	USER_GROUP_NOT_ASSIGN(10015, "用户的组织未分配！"),
	NO_DATA_PERMISSION_FOR_UPDATE(10016, "没有权限更新该数据!"),
	NO_DATA_PERMISSION_FOR_DELETE(10017, "没有权限删除该数据!"),
	INVALID_DATA_NUMBER_OF_BATCH_SIZE(10018, "非法的批量操作数量!"),
	STATE_PROCESS_OBJECT_NOT_BE_NULL(10019, "状态流对象不能为空!"),
	PROCESS_ACTION_NOT_BE_NULL(10020, "流程动作不能为空!"),
	PROCESS_OF_OBJECT_NOT_BE_NULL(10021, "对象状态流为空,请初始化对象状态流!"),
	THE_ACTION_MUST_BE_IN_OBJECT_STATE_PROCESS_ACTIONS(10022, "执行的动作{0}必须在对象的动作列表中!"),
	PROCESS_STATE_OBJECT_NOT_HAVE_THE_STATE_FIELD_NAME(10023, "状态流对象没有状态属性为{0}的属性名称!"),
	CURRENT_STATE_OF_PRCESS_STATE_OBJECT_IS_NULL(10024, "当前的流程对象状态为空,无法执行状态流!"),
	STATES_LIST_NOT_HAVE_THE_STATE(10025, "对象状态流列表不包含该状态{0}!"),
	CURRENT_STATE_CANNOT_EXECUTE_THE_ACTION_PROCESS(10026, "该动作{0}无法执行当前状态{1}的流程!"),
	PROCESS_STATE_OBJECT_CANNOT_INIT_STATE(10027, "状态流对象没有初始化状态!"),
	PROCESS_STATE_OBJECT_HAS_AN_STATE_VALUE_CANNOT_SET_INIT_STATE(10028, "状态流对象已经有一个状态，不能设置初始化状态!"),
	CURRENT_STATE_OF_OBJECT_CANNOT_EDIT(10029, "状态流对象当前状态不可编辑!"),
	CURRENT_STATE_OF_OBJECT_CANNOT_DELETE(10030, "状态流对象当前状态不可删除!"),
	OBJECT_NOT_HAVE_ID_PROPERTY_CANNOT_CHECK_BUSINESS_PERMISSION(10031, "对象ID属性不存在,不能进行业务权限校验!"),
	OBJECT_NOT_HAVE_GROUPID_PROPERTY_CANNOT_CHECK_BUSINESS_PERMISSION(10032, "对象GROUPID属性不存在,不能进行业务权限校验!"),
	OBJECT_NOT_HAVE_CREATEUSER_PROPERTY_CANNOT_CHECK_BUSINESS_PERMISSION(10033, "对象CREATEUSER属性不存在,不能进行业务权限校验!"),
	EXCEED_SQL_MAX_IN_SIZE(10034, "超过SQL的IN最大执行数量:{0}!"),
	EXCEED_FILE_MAX_UPLOAD_SIZE(10035,"超过文件最大上传大小{0}M"),
	STRINGS_NOT_BE_EMPTY(10036, "内部校验错误，字符串不能为空，无法进行后续操作!"),
	COLLECTION_NOT_BE_EMPTY(10037, "内部校验错误，集合不能为空，无法进行后续操作!"),
	
	MESSAGE_NOT_READABLE_ERROR(10038, "消息不可读取转化错误，请确认传递的消息格式是否正确!"),
	BIND_ARGUMENT_VALIDATE_ERROR(10039, "绑定参数校验错误:{0}"),
	INVALID_ARGUMENTS_EXCEPTION(10040, "{0}参数不合法，无法进行后续操作!"),
	MISSING_SERVLET_REQUEST_PARAM(10041, "缺少必需的请求参数:{0}!"),
	REQUEST_CONTENT_TYPE_NOT_MATCH(10042, "请求类型(Content-Type)[{0}]与实际接口的请求类型不匹配!"),
	INVALID_REQUEST_CONTENT_TYPE(10043, "无效的Content-Type类型!"),
	REQUEST_FILE_PARAM_EXCEPTION(10044, "请求中必须至少包含一个有效文件!"),
	SYSTEM_IS_BUSY(10045, "系统繁忙，请稍后重试!"),
	BASE_PARAM_VALID_EXCEPTION(10046, "统一验证参数异常:{0}!"),
	REQUEST_METHOD_NOT_SUPPORT(10047, "不支持当前请求类型！"),
	SQL_EXECUTE_EXCEPTION(10048, "运行SQL出现异常!"),
	NO_HANDER_FOUND_EXCEPTION(10049, "请求的方法未找到，请确认请求路径是否正确!"),
	END_TIME_MUST_BE_GREATER_THAN_START_TIME(10050, "结束时间必须大于开始时间"),
	
	NO_EMAIL_FOR_SEND(10151, "没有要发送的邮件信息!"),
    ONCE_SEND_EMAIL_NUMBER_TOO_LARGE(10152, "一次性发送邮件数量太大了，超出范围{0}!"),
    MAIL_SERVER_NOT_SETTING_CANNOT_SEND_MAIL(10153, "发送邮箱未设置，不能发送邮件!"),
    EMAIL_ATTCHMENT_SIZE_TOO_LARGE(10154, "邮件附件文件大小超出限制{0}M!"),
    EMAIL_ATTCHMENT_NUMBER_TOO_LARGE(10155, "邮件附件数量太多了，应该在{0}个以内！"),
    IDS_CONTAINS_INVALID_ID(10156, "ID列表中包含非法的ID!"),
    FILE_NOT_EXIST(100157, "待处理的文件不存在!"),
    FILE_URL_IS_INVALID(100158, "文件URL不合法，无法进行后续操作!"),
    FILE_UPLOAD_TO_FDFS_EXCEPTION(100159, "文件上传到文件服务器失败!"),
	OPERATE_DATAS_CONTAINS_INVALID_IDS(100160, "操作的数据中包含非法的ID!"),
	COLUMN_NAME_IS_NOT_VALID(100161, "列名不合法,不能以数字开头!"),
	CONDITON_OPERATOR_NOT_VALID(100162, "条件操作符不合法!"),
	BUSINESS_CONNECT_EXECUTE_ERROR(10063, "业务权限连接时解析失败, 请联系管理员!"),
	VALUE_CONTAINS_INVALID_SQL_CHARS(10164, "字符串中包含非法SQL字符!"),
	INVALID_BUSINESS_ID(10165, "非法的业务ID，请确认绑定的业务ID正确！"),
	IDEMPOTENCY_CONFLICT(100165, "执行的业务列表中包含已在执行中的业务，幂等性冲突，无法进行后续操作！"),
	DATA_HAS_BEEN_MODIFYED(100167, "数据已被修改，请刷新后重试!"),
	CAN_NOT_FIND_ID_OF_TENANT(100168, "无法找到ID:{0}对应的租户!"),
	TENANT_SUPER_USER_HAS_EXIST(100169, "租户对应超级管理员用户已存在，不能重复生成!"),
	TENANT_ID_NOT_VALID(100170, "租户ID不合法，无法进行后续操作！"),
	CAN_NOT_FIND_ROOT_GROUP_OF_TENANT(100171, "无法找到租户对应的根组织!"),
	CAN_NOT_FIND_ID_OF_HMAC_USER(100172, "无法招待ID:{0}对应的用户!"),
	CAN_NOT_FIND_ID_OF_TENANT_USER_IN_THE_GROUP(100173, "在组织ID:{0}中无法找到ID:{1}对应的租户!"),
	ROOT_GROUP_NOT_BE_DELETE(100174, "根组织无法删除！"),
	TENANT_GROUP_CODE_EXCEED(100175, "无法初始化租户编码，请联系管理员处理!"),
	TENANT_ROOT_USER_NOT_BE_DELETED(100176,"租户超级用户无法删除!"),
	CAN_NOT_FIND_ID_OF_FUNCTION(100177, "无法找到ID:{0}对应的功能!")
	;

	
	private int code;
	
	private String message;
	

	@Override
	public int getExceptionCode() {
		return this.code;
	}

	
	@Override
	public String getExceptionMessage() {
		return this.message;
	}



	ExceptionEnum(final int code, final String message){
		this.code = code;
		this.message = message;
	}
}
