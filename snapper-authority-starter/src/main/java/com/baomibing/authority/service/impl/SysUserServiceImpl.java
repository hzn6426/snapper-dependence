
package com.baomibing.authority.service.impl;


import com.baomibing.authority.action.UserAction;
import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysUser;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysUserMapper;
import com.baomibing.authority.service.*;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.context.PermContext;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.CharacterUtil;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.Patterns;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.tool.constant.NumberConstant.MAX_IN_BATCH_SIZE;

/**
 * 用户服务
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserServiceImpl extends MBaseServiceImpl<SysUserMapper, SysUser, UserDto> implements SysUserService {

	@Autowired private SysUserRoleService userRoleService;
	@Autowired private SysUserPositionService userPositionService;
	@Autowired private SysUserGroupService userGroupService;
	@Autowired private SysUserBusinessPermService userBusinessPermService;
	@Autowired private SysPositionUserEntrustService positionUserEntrustService;
	@Autowired private SysGroupService groupService;
	@Autowired private SysRoleService roleService;
	@Autowired private SysRoleResourceService roleResourceService;


	@Transactional
	@Override
	public void doSave(UserDto user) {
		if (Boolean.TRUE.equals(user.getBeMultiLogin())) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_SUPPORT_MULTI_LOGIN);
		}
//		int count = baseMapper.selectCount(lambdaQuery());
//		if (count > SystemConst.MAX_USER_NUMBER) {
//			throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_CREATE_EXCEED_USER, SystemConst.MAX_USER_NUMBER);
//		}
		SysUser dbUser = this.baseMapper.selectOne(new QueryWrapper<SysUser>().eq("user_no", user.getUserName()));
		if (Checker.beNotNull(dbUser)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_NAME_HAS_EXIST, user.getUserName());
		}
		String pinyin = CharacterUtil.getShortPinYin(user.getUserRealCnName());
		user.setPinYin(pinyin).setFullPinYin(CharacterUtil.getFullPinYin(user.getUserRealCnName()));
		StateWorkFlow.doInitState(user);
		super.saveIt(user);
	}

	@Transactional
	@Override
	public void doUpdate(UserDto user) {
		Assert.CheckArgument(user);
		Assert.CheckArgument(user.getUserName());
		if (Boolean.TRUE.equals(user.getBeMultiLogin())) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_SUPPORT_MULTI_LOGIN);
		}
//		int count = baseMapper.selectCount(lambdaQuery());
//		if (count > SystemConst.MAX_USER_NUMBER) {
//			throw new ServerRuntimeException(AuthorizationExceptionEnum.THE_VERSION_CAN_NOT_CREATE_EXCEED_USER, SystemConst.MAX_USER_NUMBER);
//		}
		UserDto dbUser = super.getIt(user.getId());
		assertBeLock(dbUser);
		user.setUserPasswd(null);
		String pinyin = CharacterUtil.getShortPinYin(user.getUserRealCnName());
		user.setPinYin(pinyin);
		super.updateIt(user);
	}

	@Override
	public UserDto getByUserNo(String userNo) {
		if (Checker.beEmpty(userNo)) {
			return null;
		}
		SysUser user = this.baseMapper.selectOne(lambdaQuery().eq(SysUser::getUserNo, userNo));
		//去掉过期的
		if (Checker.beNotNull(user)) {
			if (Checker.beNotNull(user.getExpireTime()) && user.getExpireTime().getTime() < System.currentTimeMillis()) {
				return null;
			}
		}
		return Checker.beNotNull(user) ? mapper2v(user) : null;
	}

	@Action(value = PermActionConst.USER_SEARCH, ignoreGroupScope = true)
	@ActionConnect(value = {"countByCondition","searchByCondition"}, groupAuthColumn = "ug.group_id")
	@Override
	public SearchResult<UserDto> search(UserDto user, int pageNumber, int pageSize) {
		List<UserDto> vlist = Lists.newArrayList();
		PermContext.withIgnoreCompanyScope(true);
		int count = baseMapper.countByCondition(user);
		if (count == 0) {
			return new SearchResult<>(count, emptyList());
		}
		List<SysUser> list = baseMapper.searchByCondition(user, pageSize, offset(pageNumber, pageSize));
		list.forEach(u -> {
			UserDto dto = mapper2v(u);
			vlist.add(dto);
			dto.setUserPasswd("nopassword");
		});
		return new SearchResult<>(count, vlist);
	}

	@Action(value = PermActionConst.GROUP_SEARCH_USER)
	@ActionConnect(value = {"countByGroup", "searchByGroup"}, groupAuthColumn = "ug.group_id")
	@Override
	public SearchResult<UserDto> searchByGroupCondition(UserDto user, int pageNumber, int pageSize) {
		List<UserDto> vlist = Lists.newArrayList();
		if (userNotLogin() || Checker.beEmpty(user.getGroupId())) {
			return new SearchResult<UserDto>(0, vlist);
		}
		int count = baseMapper.countByGroup(user.getGroupId());
		if (count == 0) {
			return new SearchResult<>(count, emptyList());
		}
		return new SearchResult<>(count, mapper(baseMapper.searchByGroup(user.getGroupId(), pageSize, offset(pageNumber, pageSize))));
	}

	@Override
	public SearchResult<UserDto> searchForNotAssignGroup(UserDto user, int pageNumber, int pageSize) {
		int offset = offset(pageNumber, pageSize);
		List<SysUser> list = this.baseMapper.listForNotAssignGroup(pageSize, offset);
		int count = this.baseMapper.countForNotAssignGroup();
		return new SearchResult<>(count, mapper(list));
	}

	@Transactional
	@Override
	public void deleteByIds(Set<String> uids) {
		Assert.CheckArgument(uids);
		if (MAX_IN_BATCH_SIZE < uids.size()) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_DATA_NUMBER_OF_BATCH_SIZE, MAX_IN_BATCH_SIZE);
		}
		List<UserDto> list = gets(uids);
		if (list.size() != uids.size()) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.INVALID_DATA_NUMBER_OF_BATCH_SIZE);
		}
		for (UserDto u : list) {
			StateWorkFlow.doAssertDelete(u);
			assertBeLock(u);
		}
		userRoleService.deleteByUsers(uids);
		userGroupService.deleteByUsers(uids);
		userPositionService.deleteByUsers(uids);
		userBusinessPermService.deleteByUsers(uids);
		positionUserEntrustService.deleteByUsers(uids);
		roleService.deleteDefaultsByUsers(uids);
		super.deletes(uids);
	}

	@Transactional
	@Override
	public void updateUserSecret(String userId, String secret) {
		UserDto u = super.getIt(userId);
		Assert.CheckArgument(u, AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, userId);
//		u.setSecretKey(secret);
//		super.updateIt(u);
		super.updateIt(new UserDto().setId(userId).setSecretKey(secret));
	}

	@Transactional
	@Override
	public void updatePassword(UserDto user) {
		UserDto userDto = super.getIt(user.getId());
		user.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(user.getUserPasswd())));
		user.setNewPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(user.getNewPassword())));
		if (!user.getUserPasswd().equals(userDto.getUserPasswd())) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.ORIGINAL_PASSWROD_FAIL);
		}
		user.setUserPasswd(user.getNewPassword());
		user.setNewPassword(null);
		super.updateIt(user);
	}


	@Override
	public UserDto getIt(Serializable pk) {
		return super.getIt(pk).setUserPasswd("");
	}

	private void updateStateByIds(Set<String> ids, UserAction action) {
		Assert.CheckArgument(ids);
		Assert.CheckArgument(action);
		List<UserDto> users = super.gets(ids);
		if (users.size() != ids.size()) {
			throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
		}
		for (UserDto u : users) {
			assertBeLock(u);
			StateWorkFlow.doProcess(u, action);
		}
		super.updateItBatch(users);
	}

	@Transactional
	@Override
	public void activeUsers(Set<String> ids) {
		Assert.CheckArgument(ids);
		List<UserDto> users = super.gets(ids);
		if (users.size() != ids.size()) {
			throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
		}
		for (UserDto u : users) {
			StateWorkFlow.doProcess(u, UserAction.ACTIVE);
			if (Checker.beEmpty(u.getUserEmail()) && !Patterns.VALID_EMAIL_ADDRESS_REGEX.matcher(u.getUserEmail()).find()) {
				throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_EMAIL_INVALID_CANNOT_SEND_PASSWORD_EMAIL);
			}
			//生成密码
//			String password = RandomStringUtils.randomAlphanumeric(6);
			String password = "123456";//演示环境
			u.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(password))).setUnencryptPassword(password);
		}
		super.updateItBatch(users);
		//发送邮件
//		outHandlerService.sendUserCreatedEmail(users);
	}

	@Transactional
	@Override
	public void lockUsers(Set<String> ids) {
		updateStateByIds(ids, UserAction.LOCK);
	}

	@Transactional
	@Override
	public void unlockUsers(Set<String> ids) {
		updateStateByIds(ids, UserAction.UNLOCK);

	}

	@Transactional
	@Override
	public void stopUsers(Set<String> ids) {
		updateStateByIds(ids, UserAction.STOP);

	}

	@Transactional
	@Override
	public void unstopUsers(Set<String> ids) {
		updateStateByIds(ids, UserAction.UNSTOP);

	}

	@Transactional
	@Override
	public void resetPasswd(Set<String> ids) {
		Assert.CheckArgument(ids);
		List<UserDto> users = super.gets(ids);
		if (users.size() != ids.size()) {
			throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
		}
		for (UserDto u : users) {
			assertBeLock(u);
			if (Checker.beEmpty(u.getUserEmail()) && !Patterns.VALID_EMAIL_ADDRESS_REGEX.matcher(u.getUserEmail()).find()) {
				throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_EMAIL_INVALID_CANNOT_SEND_PASSWORD_EMAIL);
			}
			//生成密码
			String password = RandomStringUtils.randomAlphanumeric(6);
			u.setUserPasswd(DigestUtils.md5Hex(DigestUtils.md5Hex(password))).setUnencryptPassword(password);
		}
		super.updateItBatch(users);
		//发送邮件
//		outHandlerService.sendUserRestPasswdEmail(users);
	}


	@Override
	public List<UserDto> listUsersByGroupId(String gid) {
		if (Checker.beEmpty(gid)) {
			return new ArrayList<>();
		}
		return mapper(baseMapper.listUsersByGroupId(gid));
	}

	@Override
	public List<UserDto> listByKeyWordAndType(String userTag, String KeyWord) {
		return mapper(super.baseMapper.listByKeyWordAndType(userTag, KeyWord));
	}

	@Override
	public List<UserDto> listByUserNos(List<String> userNos) {
		LambdaQueryWrapper<SysUser> queryWrapper = lambdaQuery();
		queryWrapper.in(SysUser::getUserNo, Sets.newHashSet(userNos));
		queryWrapper.select(SysUser::getUserRealEnName, SysUser::getUserRealCnName, SysUser::getUserNo, SysUser::getUserEmail, SysUser::getUserTag);
		return mapper(super.baseMapper.selectList(queryWrapper));
	}

	@Override
	public List<UserDto> listByKeyWordAndTypeOfCompany(String userTag, String keyWord, String companyId) {
		if (Checker.beEmpty(userTag) || Checker.beEmpty(keyWord) || Checker.beEmpty(companyId)) {
			return Lists.newArrayList();
		}
		return mapper(baseMapper.listByKeyWordAndTypeOfCompany(userTag, keyWord, companyId));
	}

	@Action(value = PermActionConst.USER_SEARCH)
	@ActionConnect(value="listAllGroupUsers", groupAuthColumn = "ug.group_id")
	@Override
	public List<UserDto> listAllGroupUsers() {
		return mapper(this.baseMapper.listAllGroupUsers());
	}

	@Override
	public List<UserDto> listAllGroupUsersByTag(String tag) {
		return mapper(baseMapper.listAllGroupUsersByTag(tag));
	}

	@Override
	public List<UserDto> listByUserCnNames(List<String> cnNames) {
		if (Checker.beEmpty(cnNames)) {
			return Lists.newArrayList();
		}
		List<UserDto> users = mapper(baseMapper.listByUserCnNames(Sets.newHashSet(cnNames)));
		Map<String, UserDto> map = users.stream().collect(Collectors.toMap(UserDto::getUserName, Function.identity(), (v1, v2) -> v1));
		return Lists.newArrayList(map.values());
	}

	@Override
	public void saveMenuPerm(String userId, String groupId, Set<String> menuIds) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(groupId);
		RoleDto role = getDefaultRoleByUser(userId, groupId);
		roleResourceService.saveMenusPermsByRole(role.getId(), menuIds);
	}

	@Override
	public void saveButtonPerm(String userId, String groupId, String menuId, Set<String> buttonIds) {
		Assert.CheckArgument(userId);
		Assert.CheckArgument(groupId);
		Assert.CheckArgument(menuId);
		RoleDto role = getDefaultRoleByUser(userId, groupId);
		roleResourceService.saveButtonPermsByMenuAndRole(role.getId(), menuId, buttonIds);
		roleResourceService.refreshPrivileges(buttonIds.toArray(new String[0]));
	}

	private RoleDto getDefaultRoleByUser(String userId, String groupId) {
		UserDto user = getIt(userId);
		if (Checker.beNull(user)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.ID_OF_USER_NOT_EXIST, userId);
		}
		List<UserGroupDto> ugs = userGroupService.listByUser(userId);
		boolean beContains = ugs.stream().anyMatch(ug -> groupId.equals(ug.getGroupId()));
		if (!beContains) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.USER_NOT_IN_THE_GROUP);
		}
        return roleService.makeDefaultRoleByUser(user.getUserName(), groupId);
	}

	@Override
	public List<String> listPermMenuIdsByUser(String userId, String groupId) {
		if (Checker.beEmpty(userId) || Checker.beEmpty(groupId)) {
			return Lists.newArrayList();
		}
		RoleDto role = getDefaultRoleByUser(userId, groupId);
		return roleResourceService.listPermResourceIdsByRoles(Sets.newHashSet(role.getId()), ResourceTypeEnum.MENU);
	}

	@Override
	public List<ButtonDto> listPermButtonsByUserAndMenu(String userId, String groupId, String menuId) {
		RoleDto role = getDefaultRoleByUser(userId, groupId);
		return roleResourceService.listPermButtonsByRolesAndMenu(Sets.newHashSet(role.getId()), menuId);
	}

	@Override
	public List<MenuDto> listPermMenusAndButtonsByUser(String userId, String groupId, String roleId, boolean beFilterPerm, boolean beFilterNoPerm) {
		List<RoleDto> roles = userRoleService.listRolesByGroupAndUser(groupId, userId);
		Set<String> roleIds = roles.stream().map(RoleDto::getId).collect(Collectors.toSet());
		if (Checker.beEmpty(roleIds) || (Checker.beNotEmpty(roleId) && !Strings.ALL.equals(roleId) && !roleIds.contains(roleId))) {
			return Lists.newArrayList();
		}
		return roleResourceService.listPermMenusAndButtonsByRoleIds(Checker.beEmpty(roleId) ? roleIds : Sets.newHashSet(roleId), beFilterPerm, beFilterNoPerm);
	}

	@Override
	public List<MenuDto> listActionMenusAndButtonsByUser(String userId, String groupId) {
		List<RoleDto> roles = userRoleService.listRolesByGroupAndUser(groupId, userId);
		return roleResourceService.listActionMenusAndButtonsByRoleIds(roles.stream().map(RoleDto::getId).collect(Collectors.toSet()));
	}
}
