package com.baomibing.gateway.spi;

public interface BusinessTenantRoleService {

    /**
     * 根据URL获取租户对应的角色列表
     * @param tenantId 租户ID
     * @param reqUrl 按钮URL
     * @param reqMethod 请求方法
     * @return
     */
    String getRolesByUrl(String tenantId, String reqUrl, String reqMethod);
}
