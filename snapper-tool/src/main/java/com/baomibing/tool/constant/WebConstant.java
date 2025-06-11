/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;

/**
 * WebConstant
 *
 * @author zening 2023/5/11 21:50
 * @version 1.0.0
 **/
public abstract class WebConstant {

    // html后缀
    public final static String SUFFIX_HTML = ".html";
    // 下载请求前缀
    public final static String PREFIX_DOWNLOAD = "download";
    // 内容类型multipart
    public final static String CONTENT_TYPE_MULTIPART = "multipart";
    // APPLICATION/JSON
    public final static String APPLICATION_JSON = "APPLICATION/JSON";

    public static final String CONTENT_TYPE = "Content-Type";

    //=======================================================================//
    //                           Filter  URL Const                           //
    //=======================================================================//

    public static final String API_USER_ADMIN_MENUS = "/api/user/listAdminMenus";

    //登录或登出URL
    public static final String API_TOKEN_URL = "/api/oauth/**";

    public static final String API_WEIXIN_URL = "/api/weixin/**";

    public static final String API_TTOKEN_URL = "/api/tauth/**";

    public static final String API_USER_LOG_URL = "/api/userLog";

    public static final String FAPI_USER_LOG_URL = "/fapi/userLog";

    public static final String FAPI_USER_AUTH_URL = "/fapi/userAuth/**";

    public static final String FAPI_SOCKET_URL = "/fapi/socket/**";
    //登录URL
    public static final String API_LOGIN_URL = "/api/oauth/token";
    //切换部门
    public static final String API_DEPATMENT_CHANGE = "/api/group/change";
    //当前用户部门列表
    public static final String API_USER_DEPARTMENTS = "/api/group/userDepartments";
    //获取菜单
    public static final String API_USER_MENUS = "/api/user/listMenus";
    //获取按钮
    public static final String API_USER_BUTTONS = "/api/user/listButtons";
    //获取当前用户信息
    public static final String API_USER_CURRENT = "/api/user/currentUser";
//	//获取所有可分配权限资源
//	public static final String API_TREE_RESOURCE_ALL = "/api/resource/treeAllMenusAndButtons";
//	//获取角色对应的资源权限
//	public static final String API_LIST_PERM_RESOURCE = "/api/resource/listPermResources";
//	//保存角色对应的资源权限
//	public static final String API_SAVE_PERM_RESOURCE = "/api/resource/saveResourcePerm";
    //根据菜单获取按钮
//	public static final String API_USER_BUTTONS = "/api/button/userMenuButtons";

    //验证用户名密码并获取组织列表
    public static final String API_VALIDATE_USER_FOR_DEPARTMENTS = "/api/user/validateUserForDepartments";

    public static final String API_LIMIT_REFRESH_CACHE = "/api/gateLimit/refreshCache";
    // feign api前缀
    public static final String FEIGN_API_PREFIX = "/fapi/";

    public static final String JWT_API_PREFIX = "/api/**";

    public final static String HMAC_API_LOG_URL = "/wapi/";

    public static final String HMAC_API_PREFIX = "/wapi/**";

    //外部第三方API
    public static final String THIRD_API_PREFIX = "/tapi/**";

    public static final String TENANT_API_PREFIX = "/eapi/**";
    //消息ping
    public static final String SOCKET_CLIENT = "/socket/**";
    public static final String SOCKET_SCHEMA = "ws";

    public static final String SOCKET_SSL_SCHEMA = "wss";

    //=======================================================================//
    //                            Tenant   Constant                          //
    //=======================================================================//
    public static final String TENANT_API_WEIXIN_URL = "/eapi/weixin/**";
    public static final String TENANT_API_REGISTER_URL = "/eapi/register/**";
    public static final String TENANT_API_TOKEN_URL = "/eapi/oauth/**";
    public static final String TENANT_API_TTOKEN_URL = "/eapi/tauth/**";
    public static final String TENANT_API_USER_LOG_URL = "/eapi/userLog";
    public static final String TENANT_API_TOKEN_LOGIN_URL = "/eapi/oauth/token";
    //切换部门
    public static final String TENANT_API_DEPATMENT_CHANGE = "/eapi/department/change";
    //当前用户部门列表
    public static final String TENANT_API_USER_DEPARTMENTS = "/eapi/group/userDepartments";
    //获取菜单
    public static final String TENANT_API_USER_MENUS = "/eapi/menu/userMenus";
    //根据菜单获取按钮
    public static final String TENANT_API_USER_BUTTONS = "/eapi/button/userMenuButtons";
    public static final String TENANT_API_USER_CURRENT = "/eapi/user/currentUser";
    public static final String TENANT_API_USER_ADMIN_MENUS = "/eapi/user/listAdminMenus";

    //验证用户名密码并获取组织列表
    public static final String TENANT_API_VALIDATE_USER_FOR_DEPARTMENTS = "/eapi/user/validateUserForDepartments";

    //=======================================================================//
    //                        Jwt Claim And Header                           //
    //=======================================================================//

    public static final int JWT_EXPIRE_IN_MILLISECONDS = 12*3600000;
    public static final String JWT_SYSTEM_TAG = "JWT_SYSTEM_TAG";
    public static final String JWT_BEAR_TYPE = "Bearer";
    public static final String JWT_USER_ID = "JWT_USER_ID";
    public static final String JWT_USER_NO = "JWT_USER_NO";
    public static final String JWT_GROUP_ID = "JWT_GROUP_ID";
    public static final String JWT_COMPANY_ID = "JWT_COMPANY_ID";
    public static final String JWT_DEPARTMENT_ID = "JWT_DEPARTMENT_ID";
    public static final String JWT_MULTI_LOGIN = "JWT_MULTI_LOGIN";
    public static final String JWT_EXPIRE_POLICY = "JWT_EXPIRE_POLICY";
    public static final String JWT_TENANT_ID = "JWT_TENANT_ID";
    public static final String JWT_ACCESS_TOKEN = "access_token";
}
