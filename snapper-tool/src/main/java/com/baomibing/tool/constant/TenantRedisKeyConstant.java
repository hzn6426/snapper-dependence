
package com.baomibing.tool.constant;

public abstract class TenantRedisKeyConstant {

    //=======================================================================//
    //                      System Module Redis Key                          //
    //=======================================================================//


    //前缀
    private static final String ODM_PREFIX = "ODM_TENANT_";


    //=======================================================================//
    //                      Limit Module Redis Key                          //
    //=======================================================================//
    public static final String KEY_RATE_LIMIT = ODM_PREFIX + "key_rate_limit";
    //=======================================================================//
    //                      Button Module Redis Key                          //
    //=======================================================================//
    //API key前缀
    public static final String CACHE_API_PREFIX = ODM_PREFIX + "auth_api_";
    //按钮权限全追
    public static final String KEY_BUTTON_PERM_CONTEXT = ODM_PREFIX + "key_button_perm_context";

    //=======================================================================//
    //                     User Context Redis Key                            //
    //=======================================================================//

    //非单点登录时存储用户的TOKEN
    public static final String KEY_USER_TOKEN = ODM_PREFIX + "key_u_token_{0}";

    //用户上下文（以HASH形式存储）
    public static final String KEY_USER_CONTEXT = ODM_PREFIX + "key_u_context_{0}";
    //用户名
    public static final String KEY_USER_NO = ODM_PREFIX + "key_u_no";
    //用户ID
    public static final String KEY_USER_ID = ODM_PREFIX + "key_u_id";
    //用户组织ID
    public static final String KEY_USER_GROUP_ID = ODM_PREFIX + "key_u_group_id";
    //用户职位ID列表
    public static final String KEY_USER_POSITION_ID = ODM_PREFIX + "key_u_position_id";
    //RSA public key
    public static final String KEY_USER_SECURITY_RSA_PK = ODM_PREFIX + "key_u_rsa_pk:{0}";
    //用户当前部门角色对应的权限authority
    public static final String KEY_USER_SECURITY_AUTHORITY = ODM_PREFIX + "key_u_authority";
    //用户角色ID
    public static final String KEY_USER_ROLE_ID = ODM_PREFIX + "key_u_role_id";
    //用户当前部门
    public static final String KEY_USER_DEPARTMENT = ODM_PREFIX + "key_u_department";
    //用户当前公司
    public static final String KEY_USER_COMPANY = ODM_PREFIX + "key_u_company";
    //用户当前邮箱信息
    public static final String KEY_USER_EMAIL = ODM_PREFIX + "key_u_email";
    //用户中文名
    public static final String KEY_USER_REAL_CN_NAME = ODM_PREFIX + "key_u_cn_name";
    //用户英文名
    public static final String KEY_USER_REAL_EN_NAME = ODM_PREFIX + "key_u_en_name";
    //字典id
    public static final String KEY_DICT_CODE = ODM_PREFIX + "key_dict_code_";
    //字典项id
    public static final String KEY_DICT_CHILD_CODE = ODM_PREFIX + "key_dict_child_code_";
    //参数id
    public static final String KEY_PARAM_ID = ODM_PREFIX + "key_param_id_";

    public static final String KEY_USER_TAG = ODM_PREFIX + "key_u_tag";

    public static final String KEY_USER_RANK = ODM_PREFIX + "key_u_rank";

    public static final String KEY_USER_SCORE = ODM_PREFIX + "key_u_score";

    public static final String KEY_USER_STATE = ODM_PREFIX + "key_u_state";

    public static final String KEY_USER_COMPANY_NAME = ODM_PREFIX + "key_u_company_name";

    public static final String KEY_USER_EXPIRE_TIME = ODM_PREFIX + "key_u_expire_time";

    public static final String KEY_PREFIX_HMAC_USER = ODM_PREFIX + "_HMAC_USER:{0}";

    public static final String CACHE_HMAC_USER_PREFIX = ODM_PREFIX + "_HMAC_USER:";

//    public static final String CACHE_API_PREFIX_USER = CACHE_API_PREFIX + "USER_{0}";

    public static final String KEY_HMAC_USER_CONTEXT = ODM_PREFIX + "key_hmac_user_context";

    public static final String KEY_TENANT_CHARGE_LOCK = ODM_PREFIX + "key_tenant_charge_lock_{0}";



    //=======================================================================//
    //                      Menu Module Redis Key                            //
    //=======================================================================//
    public static final String KEY_USER_MENU = ODM_PREFIX + "key_u_menu_{0}";

    //redis过期时间
    public static final Long REDIS_TIME_IN_SECONDS = 1800L;


    //=======================================================================//
    //                      GROUP Module Redis Key                            //
    //=======================================================================//
    public static final String GROUP_PREFIX = ODM_PREFIX + "GROUP_";
    public static final String TENANT_GROUP_PREFIX = ODM_PREFIX + "TENANT_GROUP_";

    //缓存登录用户选择的组织ID
    public static final String CACHE_USER_LOGIN_ORG_KEY =  ODM_PREFIX + "_cache_user_login_org_key_{0}";

    //=======================================================================//
    //                      Action ActionConnect Redis Key                   //
    //=======================================================================//
    public static final String CACHE_ACTION_CONNECT_PREFIX = ODM_PREFIX + "action_connect_prefix_";

    //存储 action 的缓存key
    public static final String CACHE_ACTION_CONNECT_KEY_PREFIX = ODM_PREFIX + "action_connect_key_prefix_";

    public static final String CACHE_TABLE_COLUMN_PREFIX =  ODM_PREFIX + "cache_table_column_prefix";//CACHE_TABLE_COLUMN_PREFIX_";



}
