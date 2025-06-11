/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;

public abstract class RedisKeyConstant {

    //=======================================================================//
    //                      System Module Redis Key                          //
    //=======================================================================//

//    private static String CACHE_PREFIX;

//    @Value("${snapper.cachePrefix}")
//    public void setCachePrefix(String cachePrefix) {
//        CACHE_PREFIX = cachePrefix;
//    }

    //前缀
    private static final String SNAPPER_PREFIX = "SNAPPER_";


    //=======================================================================//
    //                      Limit Module Redis Key                          //
    //=======================================================================//
    public static final String KEY_RATE_LIMIT = SNAPPER_PREFIX + "key_rate_limit";
    //=======================================================================//
    //                      Button Module Redis Key                          //
    //=======================================================================//
    //API key前缀
    public static final String CACHE_API_PREFIX = SNAPPER_PREFIX + "auth_api_";
    //按钮权限全追
    public static final String KEY_BUTTON_PERM_CONTEXT = SNAPPER_PREFIX + "key_button_perm_context";

    //=======================================================================//
    //                     User Context Redis Key                            //
    //=======================================================================//

    //非单点登录时存储用户的TOKEN
    public static final String KEY_USER_TOKEN = SNAPPER_PREFIX + "key_u_token_{0}";

    //用户上下文（以HASH形式存储）
    public static final String KEY_USER_CONTEXT = SNAPPER_PREFIX + "key_u_context_{0}";
    //用户名
    public static final String KEY_USER_NO = SNAPPER_PREFIX + "key_u_no";
    //用户ID
    public static final String KEY_USER_ID = SNAPPER_PREFIX + "key_u_id";
    //用户组织ID
    public static final String KEY_USER_GROUP_ID = SNAPPER_PREFIX + "key_u_group_id";
    //用户职位ID列表
    public static final String KEY_USER_POSITION_ID = SNAPPER_PREFIX + "key_u_position_id";
    //RSA public key
    public static final String KEY_USER_SECURITY_RSA_PK = SNAPPER_PREFIX + "key_u_rsa_pk:{0}";
    //用户当前部门角色对应的权限authority
    public static final String KEY_USER_SECURITY_AUTHORITY = SNAPPER_PREFIX + "key_u_authority";
    //用户角色ID
    public static final String KEY_USER_ROLE_ID = SNAPPER_PREFIX + "key_u_role_id";
    //用户当前部门
    public static final String KEY_USER_DEPARTMENT = SNAPPER_PREFIX + "key_u_department";
    //用户当前公司
    public static final String KEY_USER_COMPANY = SNAPPER_PREFIX + "key_u_company";
    //用户当前邮箱信息
    public static final String KEY_USER_EMAIL = SNAPPER_PREFIX + "key_u_email";
    //用户中文名
    public static final String KEY_USER_REAL_CN_NAME = SNAPPER_PREFIX + "key_u_cn_name";
    //用户英文名
    public static final String KEY_USER_REAL_EN_NAME = SNAPPER_PREFIX + "key_u_en_name";
    //字典id
    public static final String KEY_DICT_CODE = SNAPPER_PREFIX + "key_dict_code_";
    //字典项id
    public static final String KEY_DICT_CHILD_CODE = SNAPPER_PREFIX + "key_dict_child_code_";
    //参数id
    public static final String KEY_PARAM_ID = SNAPPER_PREFIX + "key_param_id_";

    public static final String KEY_USER_TAG = SNAPPER_PREFIX + "key_u_tag";

    public static final String KEY_USER_COMPANY_NAME = SNAPPER_PREFIX + "key_u_company_name";

    public static final String KEY_USER_EXPIRE_TIME = SNAPPER_PREFIX + "key_u_expire_time";

    public static final String KEY_PREFIX_HMAC_USER = SNAPPER_PREFIX + "_HMAC_USER:{0}";

    public static final String CACHE_HMAC_USER_PREFIX = SNAPPER_PREFIX + "_HMAC_USER:";

//    public static final String CACHE_API_PREFIX_USER = CACHE_API_PREFIX + "USER_{0}";

    public static final String KEY_HMAC_USER_CONTEXT = SNAPPER_PREFIX + "key_hmac_user_context";

    public static final String KEY_USER_GATE_WAY_ID = SNAPPER_PREFIX + "key_user_gate_way_id_{0}";


    //=======================================================================//
    //                      Menu Module Redis Key                            //
    //=======================================================================//
    public static final String KEY_USER_MENU = SNAPPER_PREFIX + "key_u_menu_{0}";

    //redis过期时间
    public static final Long REDIS_TIME_IN_SECONDS = 1800L;


    //=======================================================================//
    //                      GROUP Module Redis Key                            //
    //=======================================================================//
    public static final String GROUP_PREFIX = SNAPPER_PREFIX + "GROUP_";

    //缓存登录用户选择的组织ID
    public static final String CACHE_USER_LOGIN_ORG_KEY =  SNAPPER_PREFIX + "_cache_user_login_org_key_{0}";

    //=======================================================================//
    //                      Action ActionConnect Redis Key                   //
    //=======================================================================//
    public static final String CACHE_ACTION_CONNECT_PREFIX = SNAPPER_PREFIX + "action_connect_prefix_";

    //存储 action 的缓存key
    public static final String CACHE_ACTION_CONNECT_KEY_PREFIX = SNAPPER_PREFIX + "action_connect_key_prefix_";

    public static final String CACHE_TABLE_COLUMN_PREFIX =  SNAPPER_PREFIX + "cache_table_column_prefix";//CACHE_TABLE_COLUMN_PREFIX_";

    //=======================================================================//
    //                      Service Get Id Cache Redis Key                   //
    //=======================================================================//
    public static final String KEY_SERVICE_GET_ID_CACHE_PREFIX = SNAPPER_PREFIX + "key_service_get_id_cache_prefix_";

}
