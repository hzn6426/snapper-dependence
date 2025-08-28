
package com.baomibing.authority.service.impl;

import com.baomibing.authority.action.PositionAction;
import com.baomibing.authority.constant.PermActionConst;
import com.baomibing.authority.constant.PermConnectConst;
import com.baomibing.authority.constant.enums.PositionPermScopeEnum;
import com.baomibing.authority.dto.*;
import com.baomibing.authority.entity.SysPosition;
import com.baomibing.authority.exception.AuthorizationExceptionEnum;
import com.baomibing.authority.mapper.SysPositionMapper;
import com.baomibing.authority.service.*;
import com.baomibing.authority.state.PositionState;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.ActionConnect;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.process.StateWorkFlow;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysPositionServiceImpl extends MBaseServiceImpl<SysPositionMapper, SysPosition, PositionDto>
		implements SysPositionService {

	@Autowired private SysUserPositionService userPositionService;
	@Autowired private SysGroupService groupService;
	@Autowired private SysPositionUserEntrustService puserEntrustService;
	@Autowired private SysPositionGroupEntrustService pgroupEntrustService;
	@Autowired private SysPositionRoleService positionRoleService;


	@Action(value = PermActionConst.POSITION_SEARCH)
	@ActionConnect(value = {PermConnectConst.SELECT_LIST,PermConnectConst.SELECT_COUNT}, groupAuthColumn = "org_id")
	@Override
	public SearchResult<PositionDto> search(PositionDto v, int pageNumber, int pageSize) {
		if (Checker.beEmpty(v.getOrgId())) {
			return new SearchResult<>();
		}
		LambdaQueryWrapper<SysPosition> wrapper = lambdaQuery().eq(SysPosition::getOrgId, v.getOrgId());
		return super.search(wrapper, pageNumber, pageSize);
	}

	@Override
	@Transactional
	public List<PositionDto> listPositionByGroup(String groupId) {
		if (Checker.beEmpty(groupId)) {
			return Lists.newArrayList();
		}
		LambdaQueryWrapper<SysPosition> wrapper = lambdaQuery();
		wrapper.eq(SysPosition::getOrgId, groupId);
		return mapper(super.baseMapper.selectList(wrapper));
	}

	@Override
	@Transactional
	public List<PositionDto> listActivePositionByGroup(String groupId) {
		if (Checker.beEmpty(groupId))
			return Lists.newArrayList();
		LambdaQueryWrapper<SysPosition> wrapper = lambdaQuery();
		wrapper.eq(SysPosition::getOrgId, groupId).eq(SysPosition::getState, PositionState.ACTIVE);
		return mapper(super.baseMapper.selectList(wrapper));
	}

	@Transactional
	@Override
	public void savePosition(PositionDto positionDto) {
		Assert.CheckArgument(positionDto);
		GroupDto group = groupService.getIt(positionDto.getOrgId());
		if (Checker.beNull(group)) {
			throw new ServerRuntimeException(AuthorizationExceptionEnum.CANNOT_FIND_THE_ID_OF_GROUP,
					positionDto.getOrgId());
		}
		StateWorkFlow.doInitState(positionDto);
		super.saveIt(positionDto);
		// 如果职位权限范围是自定义，对自定义进行处理
		if (PositionPermScopeEnum.CUSTOMER_SPECIFIED.name().equals(positionDto.getPermScope())) {
			doUpdateEntrust(positionDto.getId(), positionDto.getEntrusts());
		}
	}

	/**
	 * 更新职位对应的委托信息
	 * 
	 * @param positionId 职位ID
	 * @param entrusts   委托列表（包含组织ID和用户ID）
	 */
	private void doUpdateEntrust(String positionId, List<String> entrusts) {
		Assert.CheckArgument(positionId);
		puserEntrustService.deleteByPosition(positionId);
		pgroupEntrustService.deleteByPosition(positionId);
		if (Checker.beEmpty(entrusts)) {
			return;
		}
		Set<String> userEntrusts = Sets.newHashSet();
		Set<String> groupEntrusts = Sets.newHashSet();
		entrusts.forEach(e -> {
			// 组织ID都是以R开头
			if (e.startsWith(Strings.R)) {
				groupEntrusts.add(e);
			} else {
				userEntrusts.add(e);
			}
		});
		List<PositionUserEntrustDto> ulist = Lists.newArrayList();
		List<PositionGroupEntrustDto> glist = Lists.newArrayList();
		userEntrusts.forEach(u -> ulist.add(new PositionUserEntrustDto().setPositionId(positionId).setUserId(u)));
		groupEntrusts.forEach(g -> glist.add(new PositionGroupEntrustDto().setGroupEntrustId(g).setPositionId(positionId)));
		if (Checker.beNotEmpty(ulist)) {
			puserEntrustService.saveItBatch(ulist);
		}
		if (Checker.beNotEmpty(glist)) {
			pgroupEntrustService.saveItBatch(glist);
		}
	}

	@Transactional
	@Override
	public void updatePosition(PositionDto positionDto) {
		Assert.CheckArgument(positionDto.getId());
		PositionDto dbPosition = super.getIt(positionDto.getId());
		assertBeLock(dbPosition);
		updateIt(positionDto);
		// 如果职位权限范围是自定义，对自定义进行处理
		if (PositionPermScopeEnum.CUSTOMER_SPECIFIED.name().equals(positionDto.getPermScope())) {
			doUpdateEntrust(positionDto.getId(), positionDto.getEntrusts());
		}
	}

	@Override
	@Transactional
	public PositionDto getPosition(String id) {
		return getIt(id);
	}

	@Transactional
	@Override
	public void doUsePosition(List<String> ids) {
		doPositionState(ids, PositionAction.USE);
	}

	@Transactional
	@Override
	public void doStopPosition(List<String> ids) {
		doPositionState(ids, PositionAction.STOP);
	}

	@Transactional
	@Override
	public void deleteByGroups(Set<String> gids) {
		List<SysPosition> positions = baseMapper.selectList(lambdaQuery().in(SysPosition::getOrgId, gids));
		if (Checker.beEmpty(positions))
			return;
		Set<String> pids = positions.stream().map(SysPosition::getId).collect(Collectors.toSet());
		baseMapper.deleteBatchIds(pids);
		userPositionService.deleteByPositions(pids);
	}


	private void doPositionState(List<String> ids, PositionAction action) {
		Assert.CheckArgument(ids);
		Assert.CheckArgument(action);
		List<PositionDto> positions = gets(new HashSet<>(ids));
		if (positions.size() != ids.size()) {
			throw new ServerRuntimeException(ExceptionEnum.IDS_CONTAINS_INVALID_ID);
		}
		for (PositionDto position : positions) {
			assertBeLock(position);
			StateWorkFlow.doProcess(position, action);
		}
		super.updateItBatch(positions);
	}

	@Transactional
	@Override
	public void deletePositions(Set<String> pids) {
		Assert.CheckArgument(pids);
		List<PositionDto> positions = gets(pids);
		for (PositionDto position : positions) {
			StateWorkFlow.doAssertDelete(position);
			assertBeLock(position);
		}
		userPositionService.deleteByPositions(pids);
		positionRoleService.deleteByPositions(pids);
		puserEntrustService.deleteByPositions(pids);
		pgroupEntrustService.deleteByPositions(pids);
		deletes(pids);
	}


	@Action(value = PermActionConst.GROUP_TREE_ALL_GROUPS_AND_POSITIONS)
	@ActionConnect(value = PermConnectConst.SELECT_LIST, groupAuthColumn = "org_id")
	@Override
	public List<PositionDto> listAllGroupPositions() {
		return mapper(this.baseMapper.selectList(lambdaQuery().isNotNull(SysPosition::getOrgId)));
	}

	@Override
	public List<String> listEntrustIdsByPosition(String positionId) {
		PositionDto post = super.getIt(positionId);
		if (PositionPermScopeEnum.CURRENT_GROUP.name().equals(post.getPermScope())) {
			return Lists.newArrayList(post.getOrgId());
		}
		if (PositionPermScopeEnum.UNDERLING_GROUP.name().equals(post.getPermScope())) {
			List<GroupDto> groups = groupService.listChildrenByParent(post.getOrgId());
			return groups.stream().map(GroupDto::getId).collect(Collectors.toList());
		}
		List<UserDto> entrustUsers = puserEntrustService.listEntrustUsersByPosition(positionId);
		// 用于渲染树，ID是唯一的，可能一个用户位于两个组织中，此处需要连接父组织ID
		List<String> entrustUids = entrustUsers.stream().map(u -> u.getGroupId() + '#' + u.getId())
				.collect(Collectors.toList());
		List<GroupDto> entrustGroups = pgroupEntrustService.listEntrustGroupsByPosition(positionId);
		List<String> entrustGids = entrustGroups.stream().map(g -> g.getId()).collect(Collectors.toList());
		entrustUids.addAll(entrustGids);
		return entrustUids;
	}
}
