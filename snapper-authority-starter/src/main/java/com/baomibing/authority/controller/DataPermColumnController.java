/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.controller;

import com.baomibing.authority.dto.DataPermColumnDto;
import com.baomibing.authority.service.SchemaService;
import com.baomibing.authority.service.SysDataPermColumnService;
import com.baomibing.orm.perm.ActionTableColumn;
import com.baomibing.orm.perm.Table;
import com.baomibing.orm.perm.TableColumn;
import com.baomibing.tool.common.PageQuery;
import com.baomibing.tool.constant.Strings;
import com.baomibing.web.annotation.ULog;
import com.baomibing.web.base.MBaseController;
import com.baomibing.web.common.R;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SchemaController
 *
 * @author zening
 * @version 1.0.0
 **/
@RequestMapping(path = "/api/dataPermColumn", consumes = {"application/json", "application/x-www-form-urlencoded"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class DataPermColumnController extends MBaseController<DataPermColumnDto> {

    @Autowired private SysDataPermColumnService dataPermColumnService;
    @Autowired private SchemaService schemaService;

    @ULog("数据列新增")
    @PostMapping
    public void saveColumns(@RequestBody List<DataPermColumnDto> columns) {
        dataPermColumnService.saveColumns(columns);
    }

    @ULog("数据列删除")
    @DeleteMapping
    public void deleteColumns(@RequestBody List<String> ids) {
        dataPermColumnService.deleteColumns(Sets.newHashSet(ids));
    }

    @GetMapping("listPermColumnsByTable")
    public List<DataPermColumnDto> listPermColumnsByTable(@RequestParam String table) {
        return dataPermColumnService.listColumnsByTable(table);
    }

    @GetMapping("listPermColumnsByTables")
    public List<DataPermColumnDto> listPermColumnsByTables(@RequestParam String table) {
        Set<String> tables = new HashSet<>(Splitter.on(Strings.COMMA).splitToList(table));
        return dataPermColumnService.listColumnsByTables(tables);
    }

    @PostMapping("fetchTables")
    public R<Table> fetchTables(@RequestBody PageQuery<Table> pager) {
        return R.build(schemaService.fetchTables(pager.getDto(), pager.getPageNo(), pager.getPageSize()));
    }

    @PostMapping("fetchColumnByTableWithPerm")
    public R<ActionTableColumn> fetchColumnByTableWithPerm(@RequestBody PageQuery<TableColumn> pager) {
        return R.build(schemaService.fetchColumnByTableWithPerm(pager.getDto(), pager.getPageNo(), pager.getPageSize()));
    }

}
