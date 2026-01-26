/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
