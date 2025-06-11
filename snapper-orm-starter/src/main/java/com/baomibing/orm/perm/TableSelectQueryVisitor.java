/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.baomibing.tool.util.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * TableAliasVisitor
 *
 * @author zening 2023/4/18 14:48
 * @version 1.0.0
 **/
public class TableSelectQueryVisitor extends MySqlASTVisitorAdapter {
    private final Map<String, MySqlSelectQueryBlock> aliasMap = new HashMap<>();

    private MySqlSelectQueryBlock fetchParent(SQLObject source) {

        SQLObject sqlObject = source.getParent();
        if (Checker.beNull(sqlObject)) return null;
        if (sqlObject instanceof MySqlSelectQueryBlock) {
            return (MySqlSelectQueryBlock) sqlObject;
        }
        return fetchParent(sqlObject);
    }

    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();
        String tableName = x.getTableName();
        MySqlSelectQueryBlock query = fetchParent(x);
        if (Checker.beNotNull(query)) {
            aliasMap.put(Checker.beEmpty(alias) ? tableName : alias, query);
        }
        return true;
    }


    public Map<String, MySqlSelectQueryBlock> getAliasMap() {
        return aliasMap;
    }

}
