
package com.baomibing.authority.service.impl;


import com.baomibing.authority.dto.UserEntrustDto;
import com.baomibing.authority.entity.SysUserEntrust;
import com.baomibing.authority.mapper.SysUserEntrustMapper;
import com.baomibing.authority.service.SysUserEntrustService;
import com.baomibing.core.annotation.NotAuthAop;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 委托用户服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserEntrustServiceImpl extends MBaseServiceImpl<SysUserEntrustMapper, SysUserEntrust, UserEntrustDto>
	implements SysUserEntrustService {

	@Transactional
	@NotAuthAop
	@Override
	public List<String> listEntrustUserIdsByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		if (Checker.beEmpty(userId) || Checker.beNull(permId) || Checker.beEmpty(orgId))
			return Lists.newArrayList();
		return this.baseMapper.listEntrustUserIdsByGroupAndUserAndPerm(orgId, userId, permId);
	}

	@Transactional
	@NotAuthAop
	@Override
	public List<String> listEntrustUserCodesByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		if (Checker.beEmpty(userId) || Checker.beNull(permId))
			return Lists.newArrayList();
		return this.baseMapper.listEntrustUserCodesByGroupAndUserAndPerm(orgId, userId, permId);
	}

	@Transactional
	@Override
	public void deleteByGroupAndUserAndPerm(String orgId, String userId, String permId) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(permId);
		baseMapper.deleteByGroupAndUserAndPerm(orgId, userId, permId);

	}

	
}
