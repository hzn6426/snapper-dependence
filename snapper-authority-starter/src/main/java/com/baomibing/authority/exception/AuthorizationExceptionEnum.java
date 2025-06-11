
package com.baomibing.authority.exception;


import com.baomibing.tool.exception.ExceptionEnumable;

public enum AuthorizationExceptionEnum implements ExceptionEnumable {

    NO_PRIVILEGE_EXCEPTION(403, "用户没有访问资源的权限!"),
    USER_BE_UNAUTHERIZED(401, "用户权限资源认证失败，没有权限!"),
    THE_VERSION_CAN_NOT_HAVE_THIS_FUNCTION(1997,"该版本无法使用该功能，请联系管理员升级版本！"),
    THE_VERSION_CAN_NOT_SUPPORT_MULTI_LOGIN(1990,"该版本无法使用同时登录功能，请联系管理员升级版本！"),
    THE_VERSION_CAN_NOT_CREATE_EXCEED_USER(1991,"该版本无法创建超过{0}个用户，请联系管理员升级版本！"),
    THE_VERSION_CAN_NOT_CREATE_EXCEED_POSITION(1992,"该版本无法创建超过{0}个职位，请联系管理员升级版本！"),
    THE_VERSION_CAN_NOT_CREATE_EXCEED_COMPANY(1993,"该版本无法创建超过{0}个分公司，请联系管理员升级版本！"),
    DECODE_RSA_EXCEPTION(2001, "解码RSA KEY失败，请重新登录!"),
    INVALID_TOKEN_EXCEPTION(20002, "鉴权失败，请重新登录!"),
    TOKEN_TIME_OUT_EXCEPTION(20003, "登录时间过长，请重新登录!"),
    UNCLEAR_USER_GROUP_EXCEPTION(20004, "未明确组织，无法进行操作，请先选择组织后再进行其他操作!"),
    SYSTEM_IS_BUSY(20005, "系统繁忙，请稍后重试!"),
    USER_LOGIN_IN_ANOTHER_PLACE(20006, "用户在其他地方登陆，请重新登录!"),

    USER_NAME_HAS_INVALID_CHARS_WILL_BE_LOCKED(10117, "用户账号中含有非法字符,不符合规范, 非法输入IP将被锁定!"),
    USER_NAME_LOCKED(10118, "用户账号已锁定, 请联系管理员!"),
    USER_IP_LOCKED(10119, "用户IP已锁定, 请联系管理员!"),
    MENU_OF_BUTTON_REFERENCE_NOT_EXIST(10120, "按钮引用的菜单不存在！"),
    INVALID_NODE_ID_EXCEPTION(10121, "非法的节点ID！{0}"),
    INVALID_ROLE_ID_EXCEPTION(10122, "非法的角色ID！{0}"),
    MENU_CODE_NOT_BE_REPEAT(10123, "菜单编码不能重复！{0}"),
    BUTTON_CODE_NOT_BE_REPEAT(10124, "按钮编码不能重复！{0}"),
    CONTAINS_INVALID_ID_IN_LIST(10125, "列表中含有无效的ID！"),
    ROLE_CODE_NOT_BE_REPEAT(10126, "角色编码不能重复！{0}"),
    NO_DATA_FOR_UPDATE(10127, "没有需要更新的数据！"),
    INVALID_DATA_NUMBER_OF_BATCH_SIZE(10128, "批量操作失败，数据条数不匹配！"),
    CANNOT_FIND_THE_CODE_OF_OBJECT(10129, "找不到对应code为{0}的对象！"),
    FILE_PATH_NOT_FOUND_EXCEPTION(10130, "找不到对应文件！"),
    GROUP_ID_RANGE_EXCEED(10131, "组织ID范围超出！"),
    CANNOT_FIND_THE_STRING_OF_ENUM(10132, "无法找到字符串{0}对应的枚举类型{1}！"),
    CANNOT_CONNECT_THE_DATABASE(10133, "无法连接数据库,ip:{0},port:{1},db:{2},user:{3},password:{4}!"),
    SQL_EXECUTE_FAIL(10134, "SQL执行错误，请检查语法！"),
    CANNOT_FIND_THE_ID_OF_GROUP(10135, "无法找到ID为{0}对应的组织！"),
    USER_NAME_HAS_EXIST(10136, "用户名{0}已存在!"),
    ID_OF_USER_NOT_EXIST(10137, "ID:{0}对应的用户不存在!"),
    INVALID_GROUP_TYPE_OF_GROUP(10138, "组织{0}对应的组织类型不合法!"),
    NOT_SUPPORT_CREATE_CHILD_OF_GROUP_TYPE(10139, "不支持创建组织类型{0}的下级类型!"),
    CANNOT_FIND_THE_ID_OF_BUSINESS_PERM(10140, "无法找到ID{0}的业务权限对象!"),
    INVALID_BUSINESS_PERM_SCOPE(10141, "非法的业务权限范围:{0}!"),
    INVALID_BUSINESS_PERM_RANGE(10143, "非法的业务权限范围!"),
    ORIGINAL_PASSWROD_FAIL(10144, "原密码错误!"),
    ONLY_COMPANY_CAN_CREATE_CHILD_COMPANY(10145, "只有上级组织为公司时才能创建子公司!"),
    INVALID_GROUP_ID_FOR_USER_CHANGE(10146, "传入用户待切换的组织ID错误，用户未在该ID对应的组织!"),
    DICT_CODE_NOT_BE_REPEAT(10147, "字典编码不能重复！{0}"),
    DICT_CODE_NOT_MODIFY(10148, "字典编码不可修改！{0}"),
    DICT_CHILD_CODE_NOT_MODIFY(10149, "字典项编码不可修改！{0}"),
    PARAM_CODE_NOT_BE_REPEAT(10150, "参数编码不能重复！{0}"),
    PARAM_CODE_NOT_MODIFY(10151, "参数编码不可修改！{0}"),
    USER_EMAIL_INVALID_CANNOT_SEND_PASSWORD_EMAIL(10152, "用户email格式错误，无法发送密码邮件!"),
    FILE_ALREADY_EXISTS(10153, "文件{0}已存在"),
	FILE_UPLOAD_FAIL(10155, "文件上传失败"),
    POSITION_NOT_MODIFY(10156,"职位{0}不可修改"),
    POSITION_NOT_EXIST(10157,"职位{0}不可存在"),
    GROUP_NOT_EXIST(10157,"组织{0}不可存在"),
    GROUP_PARENT_NOT_EXIST(10158, "组织对应的父组织不存在!"),
    GROUP_NAME_HAS_EXIST(10059, "组织名称已经存在，无法添加!"),
	GROUP_HAS_USERS_NOT_BE_DELETE(10060, "组织或子组织中存在成员，无法被删除!"),
	BUSINESS_CONNECT_EXECUTE_ERROR(10061, "业务权限连接时解析失败, 请联系管理员!"),
    MENU_HAS_CHILDREN_BUTTON_CAN_NOT_REMOVE(10062, "菜单包含按钮不能进行删除，请先删除按钮!"),
    MENU_HAS_CHILDREN_MENU_CAN_NOT_REMOVE(10063, "菜单包含子菜单不能进行删除，请先删除子菜单!"),
    USER_GROUP_NOT_CLARITY(10064, "用户组织未明确,请明确组织!"),
    SYSTEM_TAG_NOT_CLARITY(10065, "系统标识未明确,请明确系统标识!"),
    REMOTE_CALL_BUSINESS_SYSTEM_EXCEPTION(10066, "远程调用分部系统错误,错误信息为:{0}!"),
    THIS_LOGIN_MODE_NOT_ALLOW_FOR_THE_OPERATION(10067, "该登录模式不允许此操作!"),
    BUSINESS_PERM_ACTION_DUPLICATE(10068, "业务权限标识重复,无法进行后续操作!"),
    INVALID_JWT_REQUEST_PARAMS(10069, "非法的JWT请求参数!"),
    CREATE_RSA_KEY_EXCEPTION(10070, "创建RSA秘钥错误!"),
    NO_PUBLIC_SECRET_KEY_EXCEPTION(10071, "公钥匹配错误！"),
    USER_NOT_IN_THE_GROUP(10072, "用户未加入该组织，无法进行操作！"),
    ;


    AuthorizationExceptionEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    @Override
    public int getExceptionCode() {
        return code;
    }

    @Override
    public String getExceptionMessage() {
        return this.message;
    }


}
