/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.constant;


public abstract class SystemConst {

	//=======================================================================//
	//                      System Common Constants                          //
	//=======================================================================//
	public static final int MAX_USER_NUMBER = 20;
	public static final int MAX_POSITION_NUMBER = 5;
	public static final int MAX_COMPANY_NUMBER = 1;
	public static final int ORDER_ONE = 1;
	public static final int ORDER_TWO = 2;
	public static final int ORDER_THREE = 3;
	public static final int ORDER_FOUR = 4;
	public static final int ORDER_AFTER_AUTH = 10002;
	public static final int ORDER_AFTER_LIMIT= 10003;
	public static final int ORDER_AFTER_ROUTE = 10001;

	public static final String COMMA_SEPARATOR = ",";
	public static final String COMMA_DOT_SEPARATOR= ".";
	public static final String WEB_SCOKET_URL = "/SocketServer/socketJs";
	public static final long MAX_UPLOAD_FILE_SIZE = 2 * 1024 * 1024;
	public static final long MAX_DOWNLOAD_FILE_SIZE = 10 * 1024 * 1024;

	//最大尝试登录次数
	public static final int MAX_LOGIN_RETRY_TIME = 5;

	//权限缓存预热时，资源不需要权限，此时角色ID做此标记
	public static final String RESOURCE_NO_NEED_ROLE = "RESOURCE_NO_NEED_ROLE";

	//token相关URL
	public static final String API_TOKEN_URL = "/api/oauth/**";
	public static final String API_WEIXIN_URL = "/api/weixin/**";
	public static final String API_USER_LOG_URL = "/api/userLog";
	public static final String API_TOKEN_LOGIN_URL = "/api/oauth/token";

	public static final String HMAC_API_PREFIX = "/wapi/**";

	//外部第三方API
	public static final String THIRD_API_PREFIX = "/tapi/**";
	public static final String API_ROOT_URL = "/api/**";

	public static final String JWT_API_PREFIX = "/api/**";

	public static final String API_USER_ADMIN_MENUS = "/api/user/listAdminMenus";
	//获取当前用户信息
	public static final String API_USER_CURRENT = "/api/user/currentUser";

	public static final String SOCKET_CLIENT = "/socket/**";

	public static final String API_CREATE_OR_UPDATE_REPORT = "/api/report/createOrUpdateReport";
	//切换部门
	public static final String API_DEPATMENT_CHANGE = "/api/department/change";
	//当前用户部门列表
	public static final String API_USER_DEPARTMENTS = "/api/group/userDepartments";
	//获取菜单
	public static final String API_USER_MENUS = "/api/menu/userMenus";
	//根据菜单获取按钮
	public static final String API_USER_BUTTONS = "/api/button/userMenuButtons";

	//验证用户名密码并获取组织列表
	public static final String API_VALIDATE_USER_FOR_DEPARTMENTS = "/api/user/validateUserForDepartments";
	//in的最大查询数量
	public static final int MAX_IN_BATCH_SIZE = 200;

	//=======================================================================//
	//                            About IO Constant                          //
	//=======================================================================//
	/**
	 * 最大一次性发送邮件数量
	 */
	public static final int MAX_ONCE_SEND_MAIL_SIZE = 20;
	/**
	 * 最大一次性接收邮件的数量
	 */
	public static final int MAX_ONCE_RECEIVE_MAIL_SIZE = 10;
	/**
	 * 一封邮件附件的最大数量
	 */
	public static final int MAX_FILE_NUMBER_IN_ONE_MAIL = 5;
	/**
	 * 邮件发送附件的最大值
	 */
	public static final long MAX_MAIL_FILE_SIZE = 10 * 1024 * 1024;

	//=======================================================================//
	//                            Jwt author info                            //
	//=======================================================================//
	public static final String ALGORITHM_RSA = "RSA";
	public static final String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";
	public static final String RSA_PUBLIC_KEY = "RSA_PUBLIC_KEY";


	//=======================================================================//
	//                            About Jwt Constant                         //
	//=======================================================================//
	public static final String JWT_SYSTEM_TAG = "JWT_SYSTEM_TAG";
	public static final String JWT_USER_ID = "JWT_USER_ID";
	public static final String JWT_USER_NO = "JWT_USER_NO";
	public static final String JWT_GROUP_ID = "JWT_GROUP_ID";
	public static final String JWT_COMPANY_ID = "JWT_COMPANY_ID";
	public static final String JWT_DEPARTMENT_ID = "JWT_DEPARTMENT_ID";
	public static final String JWT_ROLES = "JWT_ROLES";
	public static final int JWT_EXPIRE_IN_MILLISECONDS = 12*3600000;
	public static final String JWT_HEADER = "Authorization";
	public static final String JWT_ACCESS_TOKEN = "access_token";
	public static final String JWT_BEAR_TYPE = "Bearer";

	//=======================================================================//
	//                   Quartz Job Param Constants                          //
	//=======================================================================//

	//数据库备份创建的任务组
	public static final String QUARTZ_DATABASE_DUMP_GROUP = "QUARTZ_DATABASE_DUMP_GROUP";
	//数据库备份创建的任务KEY前缀
	public static final String QUARTZ_DATABASE_DUMP_PREFIX = "QUARTZ_DATABASE_DUMP_PREFIX";

	public static final String QUARTZ_DATABASE_TRIGGER_GROUP = "QUARTZ_DATABASE_TRIGGER_GROUP";

	public static final String QUARTZ_DATABASE_TRIGGER_PREFIX = "QUARTZ_DATABASE_TRIGGER_PREFIX";
	
//	//=======================================================================//
//	//                            Jwt author info                            //
//	//=======================================================================//
//	public static final String ALGORITHM_RSA = "RSA";
//	public static final String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";
//	public static final String RSA_PUBLIC_KEY = "RSA_PUBLIC_KEY";





}
