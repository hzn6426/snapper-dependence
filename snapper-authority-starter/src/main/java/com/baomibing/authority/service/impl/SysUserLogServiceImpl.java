
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.UserLogDto;
import com.baomibing.authority.entity.SysUserLog;
import com.baomibing.authority.mapper.SysUserLogMapper;
import com.baomibing.authority.service.SysUserLogService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户日志服务实现类
 * 
 * @author zening
 * @since 1.0.0
 */
@Service
public class SysUserLogServiceImpl extends MBaseServiceImpl<SysUserLogMapper, SysUserLog, UserLogDto> implements SysUserLogService {

    @Override
	public SearchResult<UserLogDto> search(UserLogDto v, int pageNumber, int pageSize) {
		Assert.CheckArgument(v);
		Integer count = this.baseMapper.countLog(v.getState(),v.getCreateUserCnName(),v.getExchangeName(), v.getStartTime(), v.getEndTime());
		if (count == 0) {
			return new SearchResult<>(0, Lists.newLinkedList());
		}
		int offset = offset(pageNumber, pageSize);
		List<SysUserLog> logs = this.baseMapper.searchLog(v.getState(),v.getCreateUserCnName(),v.getExchangeName(),v.getStartTime(), v.getEndTime(), offset, pageSize);
		return new SearchResult<>(count, mapper(logs));
	}

	@Override
    public void doSaveLogAsync(UserLogDto userLog) {
        Assert.CheckArgument(userLog);
        super.saveIt(userLog);
    }

}
