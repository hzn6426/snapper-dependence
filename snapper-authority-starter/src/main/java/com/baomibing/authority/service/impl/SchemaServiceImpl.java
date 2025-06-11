
package com.baomibing.authority.service.impl;

import com.baomibing.authority.service.SchemaService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.perm.ActionTableColumn;
import com.baomibing.orm.perm.Table;
import com.baomibing.orm.perm.TableColumn;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.baomibing.tool.util.PageUtil.offsetCurrent;

/**
 * SchemaServiceImpl
 *
 * @author zening 2023/4/23 11:03
 * @version 1.0.0
 **/
@Service
public class SchemaServiceImpl implements SchemaService {

    @Value("${snapper.schema}")
    private String schema;

    @Override
    public List<ActionTableColumn> fetchColumnByTable(Set<String> tables, boolean beFresh) {
        List<ActionTableColumn> columns = Lists.newArrayList();
        if (Checker.beEmpty(tables)) {
            return columns;
        }
        String inSql = tables.stream().map(t -> "'" + t + "'").collect(Collectors.joining(","));
        String sql = "select t.`TABLE_NAME` as `table_name`, t.`COLUMN_NAME` as `column_name`, t.COLUMN_COMMENT as column_comment, t.DATA_TYPE as data_type" +
                " from information_schema.columns t " +
                "where table_schema='" + schema + "' and table_name in (" + inSql + ")";
        List<Map<String, Object>> list = SqlRunner.db().selectList(sql);
        if (Checker.beEmpty(list)) {
            return columns;
        }
        for (Map<String, Object> map : list) {
            ActionTableColumn tc = new ActionTableColumn();
            tc.setDataType(ObjectUtil.toStringIfNotNull(map.get("data_type")));
            tc.setColumnComment(ObjectUtil.toStringIfNotNull(map.get("column_comment")));
            tc.setColumnName(ObjectUtil.toStringIfNotNull(map.get("column_name")));
            tc.setTableName(ObjectUtil.toStringIfNotNull(map.get("table_name")));
            columns.add(tc);
        }
        return columns;
    }

    @Override
    public SearchResult<ActionTableColumn> fetchColumnByTableWithPerm(TableColumn column, int pageNo, int pageSize) {
        List<ActionTableColumn> columns = Lists.newArrayList();
        String selectSql = "select t.`TABLE_NAME` as `table_name`, t.`COLUMN_NAME` as `column_name`, t.COLUMN_COMMENT as column_comment, " +
                "t.DATA_TYPE as data_type, IF(dpc.id is null, 0, 1) as be_in_perm ";
        String countSql = "select count(1) ";
        StringBuilder sql = new StringBuilder(" from information_schema.columns t LEFT JOIN sys_data_perm_column dpc ON t.`COLUMN_NAME` = dpc.`column_name` and t.`TABLE_NAME` = dpc.`table_name` " +
                "where table_schema='" + schema + "' and t.table_name = '" + column.getTableName() + "'");

        boolean beCondition = Checker.beNotEmpty(column.getColumnName()) && Checker.beNotEmpty(column.getColumnComment());
        if (beCondition) {
            sql.append(" AND (").append(" t.`COLUMN_NAME` like '%" + column.getColumnName() + "%'").append(" OR ")
                .append(" t.`COLUMN_COMMENT` like '%" + column.getColumnComment() + "%')");
        } else if (Checker.beNotEmpty(column.getColumnName())) {
            sql.append(" and t.`COLUMN_NAME` like '%" + column.getColumnName() + "%'");
        } else if (Checker.beNotEmpty(column.getColumnComment())) {
            sql.append(" and t.`COLUMN_COMMENT` like '%" + column.getColumnComment() + "%'");
        }
        int offset = offsetCurrent(pageNo, pageSize);
        String otherSql = " order by t.`TABLE_NAME` asc limit " + offset + "," + pageSize;
        int count = SqlRunner.db().selectCount(countSql + sql.toString());
        if (count == 0) {
            return new SearchResult<>(0, Lists.newArrayList());
        }
        List<Map<String, Object>> list = SqlRunner.db().selectList(selectSql + sql.toString() + otherSql);

        for (Map<String, Object> map : list) {
            ActionTableColumn tc = new ActionTableColumn();
            tc.setDataType(ObjectUtil.toStringIfNotNull(map.get("data_type")));
            tc.setColumnComment(ObjectUtil.toStringIfNotNull(map.get("column_comment")));
            tc.setColumnName(ObjectUtil.toStringIfNotNull(map.get("column_name")));
            tc.setTableName(ObjectUtil.toStringIfNotNull(map.get("table_name")));
            tc.setBeInPerm(Strings.ONE.equals(ObjectUtil.toStringIfNotNull(map.get("be_in_perm"))));
            columns.add(tc);
        }
        return new SearchResult<>(count, columns);
    }

    @Override
    public SearchResult<Table> fetchTables(Table table, int pageNo, int pageSize) {
        List<Table> tables = Lists.newArrayList();
        String selectSql = "select t.`TABLE_NAME` as `table_name`, t.TABLE_COMMENT as `table_comment` ";
        String countSql = "select count(1) as count_no ";
        StringBuilder sql = new StringBuilder(
                "from information_schema.`TABLES` t " +
                "where " +
//                " t.`TABLE_NAME` not like 'sys%' and" +
                " t.table_schema='" + schema + "'");
        boolean beCondition = Checker.beNotEmpty(table.getTableName()) && Checker.beNotEmpty(table.getTableComment());
        if (beCondition) {
            sql.append(" AND (").append(" t.`TABLE_NAME` like '% + table.getTableName() + %'").append(" OR ").append(" t.`TABLE_COMMENT` like '%" + table.getTableComment() + "%')");
        } else if (Checker.beNotEmpty(table.getTableName())) {
            sql.append(" and t.`TABLE_NAME` like '%" + table.getTableName() + "%'");
        } else if (Checker.beNotEmpty(table.getTableComment())) {
            sql.append(" and t.`TABLE_COMMENT` like '%" + table.getTableComment() + "%'");
        }
        int offset = offsetCurrent(pageNo, pageSize);
        String otherSql = " order by t.`TABLE_NAME` asc limit " + offset + "," + pageSize;
        int count = SqlRunner.db().selectCount(countSql + sql.toString());
        if (count == 0) {
            return new SearchResult<>(0, Lists.newArrayList());
        }
        List<Map<String, Object>> list = SqlRunner.db().selectList(selectSql + sql.toString() + otherSql);
        for (Map<String, Object> map : list) {
            Table t = new Table();
            t.setTableName(ObjectUtil.toStringIfNotNull(map.get("table_name")));
            t.setTableComment(ObjectUtil.toStringIfNotNull(map.get("table_comment")));
            tables.add(t);
        }
        return new SearchResult<>(count, tables);
    }

    @Override
    public List<Table> fetTableByNames(Set<String> tableNames) {
        if (Checker.beEmpty(tableNames)) {
            return Lists.newArrayList();
        }
        String inSql = tableNames.stream().map(t -> "'" + t + "'").collect(Collectors.joining(","));
        List<Table> tables = Lists.newArrayList();
        String sql = "select t.`TABLE_NAME` as `table_name`, t.TABLE_COMMENT as `table_comment` " +
                "from information_schema.`TABLES` t " +
                "where t.`TABLE_NAME` IN (" + inSql + ") and t.table_schema='" + schema + "'";
        List<Map<String, Object>> list = SqlRunner.db().selectList(sql);

        if (Checker.beEmpty(list)) {
            return tables;
        }
        for (Map<String, Object> map : list) {
            Table t = new Table();
            t.setTableName(ObjectUtil.toStringIfNotNull(map.get("table_name")));
            t.setTableComment(ObjectUtil.toStringIfNotNull(map.get("table_comment")));
            tables.add(t);
        }
        return tables;
    }
}
