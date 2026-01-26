/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysTenantGroup;
import com.baomibing.core.wrap.GroupIntervalWrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysTenantGroupMapper extends BaseMapper<SysTenantGroup> {

    /**
     * 把右值大于<code>gleft</code>的节点的右值加上2
     * @param gleft 左节点
     */
    void updateRootAddParentRight(@Param("gleft")int gleft);

    /**
     * 把左值大于<code>gleft</code>的节点的左值加上2
     * @param gleft 左节点
     */
    void updateRootAddParentLeft(@Param("gleft")int gleft);
    /**
     * 把右值大于<code>gleft</code>的节点的右值加上2
     * @param tenantId 租户ID
     * @param gleft 左节点
     */
    void updateAddParentRight(@Param("tenantId") String tenantId, @Param("gleft")int gleft);
    /**
     * 把左值大于<code>gleft</code>的节点的左值加上2
     * @param tenantId 租户ID
     * @param gleft 左节点
     */
    void updateAddParentLeft(@Param("tenantId") String tenantId, @Param("gleft")int gleft);
    /**
     * 把右节点大于<code>gvalue</code>的右节点值减左右节点差值+1
     * @param tenantId 租户ID
     * @param gvalue 左右节点差值+1
     * @param gright 右节点
     */
    void updateSubtractNodeRight(@Param("tenantId") String tenantId, @Param("gvalue")int gvalue, @Param("gright")int gright);
    /**
     * 把右节点大于<code>gvalue</code>的左节点值减左右节点差值+1
     * @param tenantId 租户ID
     * @param gvalue 左右节点差值+1
     * @param gright 右节点
     */
    void updateSubtractNodeLeft(@Param("tenantId") String tenantId, @Param("gvalue")int gvalue, @Param("gright")int gright);
    /**
     * 删除左值在本节点之间的节点
     * @param tenantId 租户ID
     * @param gleft 左节点
     * @param gright 右节点
     */
    void deleteBetweenLeftRight(@Param("tenantId") String tenantId, @Param("gleft")int gleft, @Param("gright")int gright);

    /**
     * 获取组织区间内的所有子组织
     * @param tenantId 租户ID
     * @param groups 组织区间
     * @return
     */
    List<SysTenantGroup> listChildsByGroupIntervals(@Param("tenantId") String tenantId, @Param("groups")List<GroupIntervalWrap> groups);

    /**
     * 根据用户查询用户对应的组织
     *
     * @param tenantId 租户ID
     * @param uid 用户ID
     * @return
     */
    List<SysTenantGroup> listGroupsByUser(@Param("tenantId") String tenantId, @Param("uid")String uid);

    /**
     * 根据组织获取对应的公司
     *
     * @param tenantId 租户ID
     * @param id 组织ID
     * @return
     */
    SysTenantGroup getParentCompanyById(@Param("tenantId") String tenantId, @Param("id") String id);

    /**
     * 获取最大ID
     * @return
     */
    String getMaxId();
}
