/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.baomibing.tool.util.Checker;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TableAliasVisitor
 *
 * @author zening 2023/4/20 09:11
 * @version 1.0.0
 **/
public class TableAliasVisitor extends MySqlASTVisitorAdapter {

    @Getter
    private final Map<String, String> tableAliasMap = new HashMap<>();

    private final Map<String, String> aliasTableMap = new HashMap<>();
//    @Getter
    private final Map<String, Set<String>> tableColumnMap = new HashMap<>();
    @Getter
    private Set<String> specialColumns = Sets.newHashSet();

    private Set<String> columns = Sets.newHashSet();



    private static final MySqlSchemaStatVisitor STAT_VISITOR = new MySqlSchemaStatVisitor();
    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();
        String tableName = x.getTableName();
        if (Checker.beNotEmpty(tableName)) {
            tableAliasMap.put(Checker.beEmpty(alias) ? tableName : alias, tableName);
            aliasTableMap.put(tableName, Checker.beEmpty(alias) ? tableName : alias);
        }
        return true;
    }

    @Override
    public boolean visit(SQLSelectStatement block) {
        block.accept(STAT_VISITOR);
        return true;
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        if (x.getExpr() instanceof SQLPropertyExpr) {
            SQLPropertyExpr expr = (SQLPropertyExpr) x.getExpr();
            Set<String> columns = tableColumnMap.get(expr.getOwnerName());
            if (columns == null) {
                columns = new HashSet<>();
            }
            columns.add(Checker.beNotEmpty(x.getAlias()) ? x.getAlias() : expr.getName());
            tableColumnMap.put(expr.getOwnerName(), columns);
        } else if (x.getExpr() instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr expr = (SQLIdentifierExpr) x.getExpr();
            columns.add(expr.getName());
//            Set<String> columns = tableColumnMap.get(expr.getName());
//            if (columns == null) {
//                columns = new HashSet<>();
//            }
//            columns.add(Checker.beNotEmpty(x.getAlias()) ? x.getAlias() : expr.getName());
//            tableColumnMap.put(Lists.newArrayList(tableColumnMap.keySet()).get(0), columns);
        } else {
            if (Checker.beNotEmpty(x.getAlias())) {
                specialColumns.add(x.getAlias());
            }

        }
        return true;
    }

    public Map<String, Set<String>> getTableColumnMap() {
        if (Checker.beNotEmpty(columns) && Checker.beNotEmpty(STAT_VISITOR.getColumns())) {
            for(TableStat.Column c : STAT_VISITOR.getColumns()) {
                if (columns.contains(c.getName())) {
                    String alias = aliasTableMap.get(c.getTable());
                    if (Checker.beNotEmpty(alias)) {
                        Set<String> tcolumns = tableColumnMap.getOrDefault(alias, Sets.newHashSet());
                        tcolumns.add(c.getName());
                        tableColumnMap.put(alias, tcolumns);
                    }
                }

            }
        }
        return tableColumnMap;
    }



}
