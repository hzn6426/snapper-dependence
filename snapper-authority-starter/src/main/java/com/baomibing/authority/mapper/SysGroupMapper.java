/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.mapper;

import com.baomibing.authority.entity.SysGroup;
import com.baomibing.orm.base.MBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
 
public interface SysGroupMapper extends MBaseMapper<SysGroup> {

	/**
	 * 获取最大ID
	 * @return 最大ID
	 */
	String getMaxId();
	/**
	 * 把右值大于<code>gleft</code>的节点的右值加上2
	 * @param gleft 左节点
	 */
	void updateAddParentRight(@Param("gleft")int gleft);
	/**
	 * 把左值大于<code>gleft</code>的节点的左值加上2
	 * @param gleft 左节点
	 */
	void updateAddParentLeft(@Param("gleft")int gleft);
	/**
	 * 把右节点大于<code>gvalue</code>的右节点值减左右节点差值+1
	 * @param gvalue 左右节点差值+1
	 * @param gright 右节点
	 */
	void updateSubtractNodeRight(@Param("gvalue")int gvalue, @Param("gright")int gright);
	/**
	 * 把右节点大于<code>gvalue</code>的左节点值减左右节点差值+1
	 * @param gvalue 左右节点差值+1
	 * @param gright 右节点
	 */
	void updateSubtractNodeLeft(@Param("gvalue")int gvalue, @Param("gright")int gright);
	/**
	 * 删除左值在本节点之间的节点 
	 * @param gleft 左节点
	 * @param gright 右节点
	 */
	void deleteBetweenLeftRight(@Param("gleft")int gleft, @Param("gright")int gright);
	
//	/**
//	 * 获取组织区间内的所有子组织
//	 * @param groups 组织区间
//	 * @return
//	 */
//	List<SysGroup> listChildsByGroupIntervals(@Param("groups")List<GroupIntervalWrap> groups);

	/**
	 * 根据用户查询用户对应的组织
	 * 
	 * @param uid 用户ID
	 * @return
	 */
	List<SysGroup> listGroupsByUser(@Param("uid")String uid);
	
	/**
	 * 根据组织获取对应的公司
	 * 
	 * @param id 组织ID
	 * @return
	 */
	SysGroup getParentCompanyById(@Param("id") String id);

}
