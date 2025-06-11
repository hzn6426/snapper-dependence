
package com.baomibing.authority.service.impl;

import com.baomibing.authority.dto.DataPermColumnDto;
import com.baomibing.authority.entity.SysDataPermColumn;
import com.baomibing.authority.mapper.SysDataPermColumnMapper;
import com.baomibing.authority.service.SysDataPermColumnService;
import com.baomibing.core.common.Assert;
import com.baomibing.orm.base.MBaseServiceImpl;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * SysDataPermColumnServiceImpl
 *
 * @author zening 2023/5/6 11:25
 * @version 1.0.0
 **/
@Service
public class SysDataPermColumnServiceImpl extends MBaseServiceImpl<SysDataPermColumnMapper, SysDataPermColumn, DataPermColumnDto> implements SysDataPermColumnService {

    @Override
    public void saveColumns(List<DataPermColumnDto> columns) {
        Assert.CheckArgument(columns);
        String tableName = columns.get(0).getTableName();
        deleteByTable(tableName);
        super.saveItBatch(columns);
    }

    @Override
    public void deleteByTable(String tableName) {
        Assert.CheckArgument(tableName);
        baseMapper.delete(lambdaQuery().eq(SysDataPermColumn::getTableName, tableName));
    }

    @Override
    public void deleteColumns(Set<String> ids) {
        Assert.CheckArgument(ids);
        super.deletes(ids);
    }

    @Override
    public List<DataPermColumnDto> listColumnsByTable(String tableName) {
        if (Checker.beEmpty(tableName)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().eq(SysDataPermColumn::getTableName, tableName)));
    }

    @Override
    public List<DataPermColumnDto> listColumnsByTables(Set<String> tableNames) {
        if (Checker.beEmpty(tableNames)) {
            return Lists.newArrayList();
        }
        return mapper(baseMapper.selectList(lambdaQuery().in(SysDataPermColumn::getTableName, tableNames)));
    }
}
