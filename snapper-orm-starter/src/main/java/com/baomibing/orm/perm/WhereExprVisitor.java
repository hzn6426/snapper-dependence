package com.baomibing.orm.perm;

import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

/**
 * 获取WHERE条件中涉及的所有列名
 *
 * @author zening
 * @version 1.0.0
 **/
public class WhereExprVisitor extends MySqlASTVisitorAdapter {
    @Getter
    private Set<String> columns = Sets.newHashSet();

    private void fetchColumn(SQLBinaryOpExpr x) {
        if (x.getLeft() instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) x.getLeft();
            columns.add(propertyExpr.getName());
        } else if (x.getLeft() instanceof SQLBinaryOpExpr){
            fetchColumn((SQLBinaryOpExpr) x.getLeft());
        }

        if (x.getRight() instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) x.getLeft();
            columns.add(propertyExpr.getName());
        } else if (x.getRight() instanceof SQLBinaryOpExpr) {
            fetchColumn((SQLBinaryOpExpr) x.getRight());
        }
    }
    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        fetchColumn(x);
        return true;
    }
}
