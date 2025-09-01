/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.perm;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import com.baomibing.core.common.AdvanceSearchParser;
import com.baomibing.core.context.MapperAuthContext;
import com.baomibing.core.context.PermContext;
import com.baomibing.core.context.SqlInjectContext;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.*;
import com.baomibing.tool.constant.InjectConstant;
import com.baomibing.tool.constant.PermConstant;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.user.UserContext;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 数据拦截器，用于进行权限拦截处理
 * @author zening
 * @version 1.0.0
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class DataPermWeaver implements Interceptor {

	private final String SCOPE_CUSTOMER_SPECIFIED = "CUSTOMER_SPECIFIED";
	private final String AND = "AND";
	private final String OR = "OR";

	private final String FORCE = "FORCE";
	private final String IGNORE = "IGNORE";


	//判断是否忽略用户权限
	private boolean judgeIgnoreUserScope(EntrustWarpper ew) {

		return Boolean.TRUE.equals(ew.getBeIgnoreUserScope()) || Boolean.TRUE.equals(PermContext.beIgnoreUserScope());
	}

	//判断是否忽略组织权限
	private boolean judgeIgnoreGroupScope(EntrustWarpper ew) {

		return Boolean.TRUE.equals(ew.getBeIgnoreGroupScope()) || Boolean.TRUE.equals(PermContext.beIgnoreGroupScope());
	}

	private boolean judgeOnlyFilterCompany(EntrustWarpper ew) {

		return Boolean.TRUE.equals(ew.getBeOnlyFilterCompany()) || Boolean.TRUE.equals(PermContext.beOnlyFilterCompany());
	}

	private boolean judgeIgnoreCompanyScope(EntrustWarpper ew) {

		return Boolean.TRUE.equals(ew.getBeIgnoreCompanyScope()) || Boolean.TRUE.equals(PermContext.beIgnoreCompanyScope());
	}

	final String MAPPED_STATEMENT = "delegate.mappedStatement";
	final String BOUND_SQL = "delegate.boundSql";
	final String BOUND_SQL_SQL = "delegate.boundSql.sql";
	// 全部权限
	final String SCOPE_ALL = "SCOPE_ALL";

	private Object doInterceptor(Invocation invocation, MetaObject metaObject, MappedStatement mappedStatement, EntrustWarpper ew) throws Throwable {

		String mapperId = mappedStatement.getId();
		String injectSql = SqlInjectContext.get(mapperId);
		if (Checker.beNotEmpty(injectSql)) {
			String supportInjectValue = SqlInjectContext.get(InjectConstant.TAG_FOR_SQL_INJECT_KEY);
			if (Checker.beNotEmpty(supportInjectValue) && InjectConstant.TAG_FOR_SQL_INJECT_VALUE.equals(supportInjectValue)) {
				//原始SQL
				String originalSql = ((BoundSql) metaObject.getValue(BOUND_SQL)).getSql();
				String additionalSql = contractSql(originalSql, injectSql, false, Checker.beNull(ew) ? "" : ew.getTableNameWithAuthAppend());
				additionalSql = doHandleExceptColumns(additionalSql, ew.getExceptColumns(), ew.getTableNameWithColumnAppend());
				metaObject.setValue(BOUND_SQL_SQL, additionalSql);
			}
		}

		SqlCommandType type =  mappedStatement.getSqlCommandType();
		if (!SqlCommandType.SELECT.equals(type) && !SqlCommandType.UPDATE.equals(type) && !SqlCommandType.DELETE.equals(type)) {
			return invocation.proceed();
		}
		// //如果没有业务权限标识，直接跳过 不需要权限
		if (Checker.beNull(ew)) {
			return invocation.proceed();
		}

		//原始SQL
		String originalSql = ((BoundSql) metaObject.getValue(BOUND_SQL)).getSql();
		Statement statement = CCJSqlParserUtil.parse(originalSql);
		List<DataPermWrap> dataPerms = ObjectUtil.defaultIfNull(ew.getDataPerms(), Lists.newArrayList());

		if (Checker.beNotEmpty(ew.getScope()) && SCOPE_ALL.equals(ew.getScope()) && !ew.isBeLoginWithAuthCode()) {
			String sql = doHandleDataPerms(dataPerms, Boolean.TRUE);
			if (Checker.beNotEmpty(sql)) {
				originalSql = contractSql(originalSql, sql, false,  ew.getTableNameWithAuthAppend());
			}
			originalSql = doHandleExceptColumns(originalSql, ew.getExceptColumns(), ew.getTableNameWithColumnAppend());
			metaObject.setValue(BOUND_SQL_SQL, originalSql);
			return invocation.proceed();
		}
		if (PermContext.hasUserNos()) {
			ew.getUserNos().addAll(PermContext.listUserNos());
			//注入用户时,如果不忽略用户权限,则不过滤分公司(可以跨公司指定用户)
			if (!judgeIgnoreUserScope(ew)) {
				ew.setScope(SCOPE_CUSTOMER_SPECIFIED);
			}
		}
		if (Boolean.TRUE.equals(PermContext.beCommaInCreateUserColumn())) {
			ew.setBeUserColumnWithComma(Boolean.TRUE);
		}
		//忽略创建用户列
		if (PermContext.beIgnoreCreateUserColumn()) {
			if (Checker.beNotEmpty(ew.getUserColumn())) {
				ew.setUserColumn(Arrays.stream(ew.getUserColumn()).filter(uc -> !uc.equals(PermConstant.CREATE_USER)).toArray(String[]::new));
			}
		}
		String mainTableName = Checker.beNotEmpty(ew.getTableNameWithAuthAppend()) ? ew.getTableNameWithAuthAppend() : getFromTableNameFromSql(statement);
		//如果有组织委托，根据表明构建组织ID对应的列名
		if (Checker.beNotEmpty(ew.getGroupWraps())) {
			originalSql = bindJoin(mainTableName, statement);
		}

		String additionalSql = getBusinessAuthSql(mainTableName, ew);
		if (Checker.beEmpty(additionalSql)) {
			return invocation.proceed();
		}

		originalSql = contractSql(originalSql, additionalSql,false, ew.getTableNameWithAuthAppend());
		originalSql = doHandleExceptColumns(originalSql, ew.getExceptColumns(), ew.getTableNameWithColumnAppend());
		metaObject.setValue(BOUND_SQL_SQL, originalSql);

		Object result = invocation.proceed();
		if (SqlCommandType.SELECT.equals(type)) {
			return result;
		}
		boolean notValid = result != null && Strings.ZERO.equals(result.toString());
		if (SqlCommandType.UPDATE.equals(type) && notValid) {
			throw new ServerRuntimeException(ExceptionEnum.NO_DATA_PERMISSION_FOR_UPDATE);
		}
		if (SqlCommandType.DELETE.equals(type) && notValid) {
			throw new ServerRuntimeException(ExceptionEnum.NO_DATA_PERMISSION_FOR_DELETE);
		}
		return result;
	}

	private String getMainTableName( MetaObject metaObject) throws Throwable {
		String originalSql = ((BoundSql) metaObject.getValue(BOUND_SQL)).getSql();
		Statement statement = CCJSqlParserUtil.parse(originalSql);
		return getFromTableNameFromSql(statement);
	}



	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//获取statement及mapper信息
		StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
		MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue(MAPPED_STATEMENT);
		String mapperId = mappedStatement.getId();
		String injectSql = SqlInjectContext.get(mapperId);

		EntrustWarpper ew = MapperAuthContext.get(mapperId);
		return doInterceptor(invocation, metaObject, mappedStatement, ew);
	}

	private final String USER_TABLE = "sys_user";
	private final String USER_GROUP_TABLE_ALIAS = "data_auth_alias_user_group";

	//构建join条件连接用户组织关系
	private String bindJoin(String mainTableName, Statement statement) {
		List<Join> joins = Lists.newArrayList();
		if (USER_TABLE.equalsIgnoreCase(mainTableName)) {//是用户表，与group表连接需要中间表
			Join ugJoin = new Join();
			String USER_GROUP_TABLE = "sys_user_group";
			Table ugTable = new Table(USER_GROUP_TABLE);
			ugTable.setAlias(new Alias(USER_GROUP_TABLE_ALIAS, false));
			ugJoin.setLeft(true);
			ugJoin.setRightItem(ugTable);
			BinaryExpression ugOnExpression = new EqualsTo();
			String USER_GROUP_TABLE_USER_ID_COLUMN = USER_GROUP_TABLE_ALIAS + ".user_id";
			ugOnExpression.setLeftExpression(new Column(USER_GROUP_TABLE_USER_ID_COLUMN));
			ugOnExpression.setRightExpression(new Column(mainTableName + ".id"));
			ugJoin.setOnExpression(ugOnExpression);
			joins.add(ugJoin);
		}

		if (Checker.beNotEmpty(joins)) {
			if (statement instanceof Select) {
				Select select = (Select)statement;
				SelectBody selectBody = select.getSelectBody();
				PlainSelect plainSelect = (PlainSelect)selectBody;
				joins.addAll(plainSelect.getJoins());
				plainSelect.setJoins(joins);
				select.setSelectBody(plainSelect);
			} else if (statement instanceof Update) {
				Update update = (Update) statement;
				joins.addAll(update.getJoins());
				update.setJoins(joins);
			} else if (statement instanceof Delete) {
				Delete delete = (Delete) statement;
				joins.addAll(delete.getJoins());
				delete.setJoins(joins);
			}
		}
		return statement.toString();
	}


	private String getFromTableNameFromSubSelect(SubSelect select) {
		String tableName = null;
		SelectBody selectBody = select.getSelectBody();
		PlainSelect plainSelect = (PlainSelect) selectBody;
		FromItem fromItem = plainSelect.getFromItem();
		if (fromItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) fromItem;
			return getFromTableNameFromSubSelect(subSelect);
		}
		Table table = (Table) plainSelect.getFromItem();
		if (Checker.beNotNull(table) && Checker.beNotNull(table.getAlias())) {
			tableName = table.getAlias().getName();
		}
		return tableName;
	}

	//获取主表名
	private String getFromTableNameFromSql(Statement statement) {
		String tableName = null;
		Table table = null;
		if (statement instanceof Select) {
			Select select = (Select) statement;
			SelectBody selectBody = select.getSelectBody();
			PlainSelect plainSelect = (PlainSelect) selectBody;
			FromItem fromItem = plainSelect.getFromItem();
			//from (select xx) 结构
			if (fromItem instanceof SubSelect) {
				SubSelect subSelect = (SubSelect) fromItem;
				tableName = getFromTableNameFromSubSelect(subSelect);
			} else {
				table = (Table) plainSelect.getFromItem();
			}
		} else if (statement instanceof Update) {
			Update update = (Update) statement;
			table = (Table) update.getFromItem();
		}  else if (statement instanceof Delete) {
			Delete delete = (Delete) statement;
			table = delete.getTable();
		}

		if (Checker.beNotNull(table) && Checker.beNotNull(table.getAlias())) {
			tableName = table.getAlias().getName();
		}
		if (Checker.beEmpty(tableName)) {
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			List<String> tableList = tablesNamesFinder.getTableList(statement);
			tableName = tableList.get(0);
		}
		return tableName;
	}

	private String buildUserColumnSQL(final String tableName, final String[] userColumns, Set<String> userNos, Boolean beNotIn, Boolean beUserColumnInComma) {
		StringBuilder sqlBuilder = new StringBuilder();
		String sql1 = "{0} ";
		String sql2 = "SUBSTR({0}, LOCATE('''#''', {0}) + 1 ) ";
		Set<String> columns = Sets.newHashSet();
		for (String userColumn : userColumns) {
			if (userColumn.contains(PermConstant.COLUMN_IGNORE)) {
				continue;
			}
			String tableUserColumn;
			boolean beCreateUserColumn;
			//是否包含点号,包含点号表示手动指定表名
			if (userColumn.contains(Strings.DOT)) {
				tableUserColumn = userColumn;
				beCreateUserColumn = userColumn.contains(PermConstant.CREATE_USER);
			} else {
				tableUserColumn = tableName + "." + userColumn;
				beCreateUserColumn = PermConstant.CREATE_USER.equalsIgnoreCase(userColumn);
			}
			columns.add(MessageFormat.format(beCreateUserColumn ? sql1 : sql2, tableUserColumn));
		}
		sqlBuilder.append(" ( ");
		Iterator<String> columnsIt = columns.iterator();
		while (columnsIt.hasNext()) {
			String c = columnsIt.next();
			if (Boolean.TRUE.equals(beUserColumnInComma)) {
				Iterator<String> userNosIt = userNos.iterator();
				sqlBuilder.append(beNotIn ? " NOT " : "");
				sqlBuilder.append(" ( ");
				while (userNosIt.hasNext()) {
					sqlBuilder.append(" FIND_IN_SET('").append(userNosIt.next()).append("',").append(c).append(")")
							.append(userNosIt.hasNext() ? " OR " : "");
				}
				sqlBuilder.append(" ) ");
			} else {
				sqlBuilder.append(c).append(beNotIn ? " NOT " : "").append(" IN ")
						.append("(")
						.append(userNos.stream().map(u -> "'" + u + "'").collect(Collectors.joining(",")))
						.append(")");
			}
			if (columnsIt.hasNext()) {
				sqlBuilder.append(beNotIn ? " AND " : " OR ");
			}
		}
		sqlBuilder.append(" ) ");
		return sqlBuilder.toString();
	}

	private String buildGroupColumnSQL(final String tableName, final String[] groupColumns, List<GroupIntervalWrap> groupWraps, boolean beNotLike) {
		Iterator<GroupIntervalWrap> groupIt = groupWraps.iterator();
		boolean beUserTable = USER_TABLE.equalsIgnoreCase(tableName);
		String sql1 = "{0}";
		String sql2 = "SUBSTR({0}, 1, IF(LOCATE('''#''', {0}), LOCATE('''#''', {0}) - 1 , LENGTH({0})))";
		Set<String> columns = Sets.newHashSet();
		for (String groupColumn : groupColumns) {
			if (groupColumn.contains(PermConstant.COLUMN_IGNORE)) {
				continue;
			}
			String tableGroupColumn;
			boolean beGroupIdColumn;

			if (groupColumn.contains(Strings.DOT)) {
				tableGroupColumn = groupColumn;
				beGroupIdColumn = tableGroupColumn.contains(PermConstant.GROUP_ID);
			} else {
				tableGroupColumn = (beUserTable ? USER_GROUP_TABLE_ALIAS : (tableName)) + "." + groupColumn;
				beGroupIdColumn = PermConstant.GROUP_ID.equalsIgnoreCase(groupColumn);
			}
			columns.add(MessageFormat.format(beGroupIdColumn ? sql1 : sql2, tableGroupColumn));
		}
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(" ( ");

		while (groupIt.hasNext()) {
			GroupIntervalWrap gw = groupIt.next();
			Iterator<String> columnsIt = columns.iterator();
			while (columnsIt.hasNext()) {
				String c = columnsIt.next();
				sqlBuilder.append(c).append(beNotLike ? " NOT " : "").append(" LIKE '").append(gw.getId()).append("%' ");
				if (columnsIt.hasNext()) {
					sqlBuilder.append(beNotLike ? " AND " : " OR ");
				}
			}
			if (groupIt.hasNext()) {
				sqlBuilder.append(" OR ");
			}
		}
		sqlBuilder.append(" ) ");
		return sqlBuilder.toString();
	}

	//构建用户委托条件
	private String buildEntrustUserCondition(String tableName, EntrustWarpper ew) {
		return buildUserColumnSQL(tableName, ew.getUserColumn(), Sets.newHashSet(ew.getUserNos()), false, ew.isBeUserColumnWithComma());
	}

	private String buildExceptEntrustUserCondition(String tableName, EntrustWarpper ew) {
		return buildUserColumnSQL(tableName, ew.getUserColumn(), Sets.newHashSet(ew.getExceptUserNos()), true, ew.isBeUserColumnWithComma());
	}

	private String buildCompanyScopeCondition(String tableName, EntrustWarpper ew) {
		return buildGroupColumnSQL(tableName, ew.getGroupColumn(), Lists.newArrayList(ew.getCompanyWrap()), false);
	}

	private String buildOnlyFilterCompanyScopeCondition(String tableName, EntrustWarpper ew) {
		return buildGroupColumnSQL(tableName, ew.getGroupColumn(), ew.getCompanyGroupWraps(), false);
	}


	private String buildExceptEntrustGroupCondition(String tableName, EntrustWarpper ew) {
		return buildGroupColumnSQL(tableName, ew.getGroupColumn(), Lists.newArrayList(ew.getExceptGroupWraps()), true);
	}

	private String buildEntrustGroupCondition(String tableName, EntrustWarpper ew) {
		return buildGroupColumnSQL(tableName, ew.getGroupColumn(), ew.getGroupWraps(), false);
	}

	//构建组织委托条件

	private String currentUser() {
		return UserContext.currentUserName();
	}

	//获取业务权限构建的SQL
	private String getBusinessAuthSql(String tableName, EntrustWarpper ew) {

		//非原始组织列(groupId)和用户列(createUser)映射 会出现 groupId#userCode(例如R010101#caozuo-M)方式，此时将分别提取用户和组织
		StringBuilder sqlBuilder = new StringBuilder("(");

		boolean beFilterCompany = !judgeIgnoreCompanyScope(ew) && Checker.beNotNull(ew.getCompanyWrap()) && !ew.getScope().equals(SCOPE_CUSTOMER_SPECIFIED);

		boolean beNeedFilterUserAndGroup = true;
		//如果是授权用户 只过滤当前用户
		if (ew.isBeLoginWithAuthCode()) {
			List<String> fakeUserNos = Lists.newArrayList(currentUser());
			ew.setUserNos(fakeUserNos);
			sqlBuilder.append("(");
			sqlBuilder.append(buildEntrustUserCondition(tableName, ew));
			sqlBuilder.append(")");
			beNeedFilterUserAndGroup = false;
		}
		//是否仅仅过滤公司
		if (beNeedFilterUserAndGroup && judgeOnlyFilterCompany(ew)) {
			beNeedFilterUserAndGroup = false;
			sqlBuilder.append("(");
			sqlBuilder.append(buildOnlyFilterCompanyScopeCondition(tableName, ew));
			sqlBuilder.append(")");
			sqlBuilder.append(doHandleDataPerms(ew.getDataPerms(), true));
			sqlBuilder.append(")");
			return sqlBuilder.toString();
		}

		List<GroupIntervalWrap> groups = ew.getGroupWraps();
		List<String> userNos = ew.getUserNos();
		List<GroupIntervalWrap> exceptGroups = ew.getExceptGroupWraps();
		List<String> exceptUserNos = ew.getExceptUserNos();
//		List<DataPermWrap> dataPerms = Checker.beNotEmpty(ew.getDataPerms()) ? ew.getDataPerms() : Lists.newArrayList();
//		dataPerms.sort(Ordering.natural().onResultOf(DataPermWrap::getBeOrCondition));
		boolean beNeedNested = beFilterCompany || beNeedFilterUserAndGroup;
		if (beNeedNested) {
			sqlBuilder.append("(");
		}

		if (beNeedFilterUserAndGroup) {

			boolean beGroupColumnIgnore = judgeIgnoreGroupScope(ew);
			boolean beUserColumnIgnore = judgeIgnoreUserScope(ew);

			if (beUserColumnIgnore && beGroupColumnIgnore) {
				beNeedFilterUserAndGroup = false;
			} else if (beUserColumnIgnore) {


				boolean beGroupEntrust = false;
				if (Checker.beNotEmpty(groups)) {
					beGroupEntrust = true;
					sqlBuilder.append("(");
					sqlBuilder.append(buildEntrustGroupCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				boolean beExceptGroupEntrust = false;
				if (Checker.beNotEmpty(exceptGroups)) {
					beExceptGroupEntrust = true;
					if (beGroupEntrust) {
						sqlBuilder.append(" AND ");
					}
					sqlBuilder.append("(");
					sqlBuilder.append(buildExceptEntrustGroupCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				beNeedFilterUserAndGroup = beGroupEntrust || beExceptGroupEntrust;
//				if (beNeedFilterUserAndGroup) {
//					for (DataPermWrap dp : dataPerms) {
//						sqlBuilder.append(dp.getBeOrCondition() ? " OR " : " AND ").append(AdvanceSearchParser.toSQL(dp.getPermExpresses()));
//					}
//				}

			} else if (beGroupColumnIgnore) {

				boolean beUserEntrust = false;

				if (Checker.beNotEmpty(userNos)) {
					beUserEntrust = true;
					sqlBuilder.append("(");
					sqlBuilder.append(buildEntrustUserCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				boolean beExceptUserEntrust = false;

				if (Checker.beNotEmpty(exceptUserNos)) {
					beExceptUserEntrust = true;
					if (beUserEntrust) {
						sqlBuilder.append(" AND ");
					}
					sqlBuilder.append("(");
					sqlBuilder.append(buildExceptEntrustUserCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				beNeedFilterUserAndGroup = beUserEntrust || beExceptUserEntrust;
//				if (beNeedFilterUserAndGroup) {
//					for (DataPermWrap dp : dataPerms) {
//						sqlBuilder.append(dp.getBeOrCondition() ? " OR " : " AND ").append(AdvanceSearchParser.toSQL(dp.getPermExpresses()));
//					}
//				}

			} else {

				boolean beUserEntrust = false, beExceptUserEntrust = false,
						beGroupEntrust = false, beExceptGroupEntrust = false;

				if (Checker.beNotEmpty(userNos) && Checker.beNotEmpty(groups)) {
					beUserEntrust = true;
					beGroupEntrust = true;
					sqlBuilder.append("((");
					sqlBuilder.append(buildEntrustUserCondition(tableName, ew));
					sqlBuilder.append(") OR (");
					sqlBuilder.append(buildEntrustGroupCondition(tableName, ew));
					sqlBuilder.append("))");
				} else if (Checker.beNotEmpty(userNos)) {
					beUserEntrust = true;
					sqlBuilder.append("(");
					sqlBuilder.append(buildEntrustUserCondition(tableName, ew));
					sqlBuilder.append(")");
				} else if (Checker.beNotEmpty(groups)) {
					beGroupEntrust = true;
					sqlBuilder.append("(");
					sqlBuilder.append(buildEntrustGroupCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				if (Checker.beNotEmpty(exceptUserNos)) {
					beExceptUserEntrust = true;
					if (beUserEntrust || beGroupEntrust) {
						sqlBuilder.append(" AND ");
					}
					sqlBuilder.append("(");
					sqlBuilder.append(buildExceptEntrustUserCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				if (Checker.beNotEmpty(exceptGroups)) {
					beExceptGroupEntrust = true;
					if (beUserEntrust || beGroupEntrust || beExceptUserEntrust) {
						sqlBuilder.append(" AND ");
					}
					sqlBuilder.append("(");
					sqlBuilder.append(buildExceptEntrustGroupCondition(tableName, ew));
					sqlBuilder.append(")");
				}

				beNeedFilterUserAndGroup = beUserEntrust || beGroupEntrust || beExceptUserEntrust || beExceptGroupEntrust;
//				if (beNeedFilterUserAndGroup) {
//					for (DataPermWrap dp : dataPerms) {
//						sqlBuilder.append(dp.getBeOrCondition() ? " OR " : " AND ").append(AdvanceSearchParser.toSQL(dp.getPermExpresses()));
//					}
//				}
			}
		}

		if (beFilterCompany) {//授权为自定义设置时,不过滤本公司
			if (beNeedFilterUserAndGroup) {
				sqlBuilder.append(" AND ");
			}
			String companySql = buildCompanyScopeCondition(tableName, ew);
			sqlBuilder.append(companySql);
//			if (!beNeedFilterUserAndGroup) {
//				for (DataPermWrap dp : dataPerms) {
//					sqlBuilder.append(dp.getBeOrCondition() ? " OR " : " AND ").append(AdvanceSearchParser.toSQL(dp.getPermExpresses()));
//				}
//			}
		}

		if (beNeedNested) {
			sqlBuilder.append(")");
		}
		sqlBuilder.append(doHandleDataPerms(ew.getDataPerms(), false));
		sqlBuilder.append(")");
		return sqlBuilder.toString();
	}

	//组合where条件
	private SQLExpr buildAndWhere(SQLExpr whereExpr, SQLExpr constraintsExpr) {
		if (Checker.beNull(whereExpr)) {
			return constraintsExpr;
		}
		return new SQLBinaryOpExpr(whereExpr, SQLBinaryOperator.BooleanAnd, constraintsExpr);
	}

	private SQLExpr buildOrWhere(SQLExpr whereExpr, SQLExpr constraintsExpr) {
		if (Checker.beNull(whereExpr)) {
			return constraintsExpr;
		}
		return new SQLBinaryOpExpr(whereExpr, SQLBinaryOperator.BooleanOr, constraintsExpr);
	}

	private void doExceptColumns(SQLSelectQueryBlock selectQueryBlock, List<ColumnPermWrap> exceptColumns) {
		if (Checker.beNull(selectQueryBlock) || Checker.beEmpty(exceptColumns)) {
			return;
		}
		Set<String> excepts = exceptColumns.stream().map(t -> (t.getBeSpecial() ? "" : t.getTableName() + Strings.DOT) + t.getColumnName()).collect(Collectors.toSet());
		List<SQLSelectItem> selectItems = selectQueryBlock.getSelectList();
		Iterator<SQLSelectItem> it = selectItems.iterator();
		while(it.hasNext()) {
			SQLSelectItem selectItem = it.next();
			if (selectItem.getExpr() instanceof SQLPropertyExpr) {
				SQLPropertyExpr expr = (SQLPropertyExpr) selectItem.getExpr();
				String tableName = expr.getOwnerName();
				String aliasName = selectItem.getAlias();
				String columnName = Checker.beNotEmpty(aliasName) ? aliasName : tableName + Strings.DOT + expr.getName();
				if (excepts.contains(columnName)) {
					it.remove();
				}
			} else if (selectItem.getExpr() instanceof SQLIdentifierExpr) {
				SQLIdentifierExpr expr = (SQLIdentifierExpr) selectItem.getExpr();
				String name = expr.getName();
				boolean beContains = excepts.stream().anyMatch(t -> t.contains(name));
				if (beContains) {
					it.remove();
				}
			} else {
				if(Checker.beNotEmpty(selectItem.getAlias())) {
					String alias = selectItem.getAlias();
					if (excepts.contains(alias)) {
						it.remove();
					}
				}
			}
		}
	}


	private String doHandleDataPerms(List<DataPermWrap> dataPerms, boolean beAnd) {
		if (Checker.beEmpty(dataPerms)) {
			return Strings.EMPTY;
		}
		final String AND = " AND ";
		final String OR = " OR ";

		StringBuilder sb = new StringBuilder();
		for (DataPermWrap dp : dataPerms) {
			List<DataPermSearchWrap> searchWraps = dp.getSearchWraps();
			StringBuilder innerSb = new StringBuilder();
			boolean beFirst = true;
			for (DataPermSearchWrap search : searchWraps) {
				String permSql = AdvanceSearchParser.toSQL(search.getPermExpresses());
				String alias = ObjectUtil.defaultIfNull(search.getAliasName(), search.getTableName());
				if (Checker.beNotEmpty(search.getConditionTableAlias())) {
					String olds = alias + Strings.DOT;
					String news = search.getConditionTableAlias() + Strings.DOT;
					permSql = StringUtils.replace(permSql, olds, news);
				}
				if (beFirst) {
					beFirst = false;
					innerSb.append(permSql);
				} else {
					innerSb.append(AND).append(permSql);
				}
			}
			sb.append(beAnd ? AND : (dp.getBeOrCondition() ? OR : AND) + Strings.LEFT_BRACKET + innerSb.toString() + Strings.RIGHT_BRACKET);
		}
		return sb.toString();
	}

	private String doHandleExceptColumnAlone(String originalSql, List<ColumnPermWrap> exceptColumns,  String tableNameInject) {
		SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(originalSql, JdbcUtils.MYSQL);
		List<SQLStatement> stmtList = parser.parseStatementList();
		SQLStatement stmt = stmtList.get(0);
		boolean beIgnore = !(stmt instanceof SQLSelectStatement);
		if (beIgnore) {
			return originalSql;
		}
		SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
		SQLSelect sqlselect = selectStmt.getSelect();
		SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

		if (Checker.beNotEmpty(tableNameInject)) {
			TableSelectQueryVisitor visitor = new TableSelectQueryVisitor();
			selectStmt.accept(visitor);
			MySqlSelectQueryBlock targetSelect = visitor.getAliasMap().get(tableNameInject);
			doExceptColumns(targetSelect, exceptColumns);
		} else {
			doExceptColumns(query, exceptColumns);
		}
		return sqlselect.toString();
	}

	private String removeDuplicateColumnByLinker(Set<String> columns, String sql, String andOr) {
		String linker = Strings.SPACE + andOr + Strings.SPACE;
		sql = sql.toLowerCase();
		if (sql.contains(linker)) {
			List<String> andSqlList = Lists.newArrayList(Splitter.on(linker).splitToList(sql));
			Iterator<String> andIt = andSqlList.iterator();
			while (andIt.hasNext()) {
				String s = andIt.next();
				boolean anyMatch = columns.stream().anyMatch(s::contains);
				if (anyMatch) {
					andIt.remove();
				}
			}
			sql = String.join(linker, andSqlList);
		}
		return sql;
	}

	//SQL中删除 与原始where中包含相同列的条件
	private String removeDuplicateColumnForSql(SQLExpr whereExpr, String sql) {
		WhereExprVisitor whereExprVisitor = new WhereExprVisitor();
		whereExpr.accept(whereExprVisitor);
		Set<String> columns = whereExprVisitor.getColumns();

		sql = removeDuplicateColumnByLinker(columns, sql, AND);
		sql = removeDuplicateColumnByLinker(columns, sql, OR);

		boolean beContains = columns.stream().anyMatch(sql::contains);
		if (beContains) {
			return Strings.EMPTY;
		}
		return sql;
	}

	private SQLExpr buildSQLExpr(String sqlSegment, DbType dbType) {
		return SQLParserUtils.createExprParser(sqlSegment, dbType).expr();
	}

	private SQLStatement buildSQLStatement(String sqlSegment, DbType dbType) {
		SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sqlSegment, dbType);
		List<SQLStatement> stmtList = parser.parseStatementList();
		return stmtList.get(0);
	}

	private String contractDataPermsSql(String originalSql, String additionalSql, boolean beOr, boolean beForceCondition, String tableNameInject) {
		SQLStatement stmt = buildSQLStatement(originalSql, JdbcUtils.MYSQL);
		SQLExpr constraintsExpr = buildSQLExpr(additionalSql, JdbcUtils.MYSQL);
		SQLExpr whereExpr;

		if (stmt instanceof SQLSelectStatement) {
			SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
			SQLSelect sqlselect = selectStmt.getSelect();
			SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

			if (Checker.beNotEmpty(tableNameInject)) {
				TableSelectQueryVisitor visitor = new TableSelectQueryVisitor();
				selectStmt.accept(visitor);
				MySqlSelectQueryBlock targetSelect = visitor.getAliasMap().get(tableNameInject);
				if (Checker.beNotNull(targetSelect)) {
					whereExpr = targetSelect.getWhere();
					if (beForceCondition) {
						targetSelect.setWhere(beOr ? buildOrWhere(whereExpr, buildSQLExpr(additionalSql, JdbcUtils.MYSQL)) : buildAndWhere(whereExpr, buildSQLExpr(additionalSql, JdbcUtils.MYSQL)));
					} else {
						String sql = removeDuplicateColumnForSql(whereExpr, additionalSql);
						if (Checker.beNotEmpty(sql)) {
							targetSelect.setWhere(beOr ? buildOrWhere(whereExpr, buildSQLExpr(sql, JdbcUtils.MYSQL)) : buildAndWhere(whereExpr, buildSQLExpr(sql, JdbcUtils.MYSQL)));
						}
					}
				} else {
					SQLExpr noEqualExpr = buildSQLExpr(" 1 = 0 ", JdbcUtils.MYSQL);
					whereExpr = query.getWhere();
					query.setWhere(beOr ? buildOrWhere(whereExpr, noEqualExpr) : buildAndWhere(whereExpr, noEqualExpr));
				}
			} else {
				whereExpr = query.getWhere();
				if (beForceCondition) {
					query.setWhere(beOr ? buildOrWhere(whereExpr, buildSQLExpr(additionalSql, JdbcUtils.MYSQL)) : buildAndWhere(whereExpr, buildSQLExpr(additionalSql, JdbcUtils.MYSQL)));
				} else {
					String sql = removeDuplicateColumnForSql(whereExpr, additionalSql);
					if (Checker.beNotEmpty(sql)) {
						query.setWhere(beOr ? buildOrWhere(whereExpr, buildSQLExpr(sql, JdbcUtils.MYSQL)) : buildAndWhere(whereExpr, buildSQLExpr(sql, JdbcUtils.MYSQL)));
					}
				}
				sqlselect.setQuery(query);
			}

			return sqlselect.toString();
		} else if (stmt instanceof SQLUpdateStatement) {
			SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
			whereExpr = updateStmt.getWhere();
			updateStmt.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
			return updateStmt.toString();
		} else if (stmt instanceof SQLDeleteStatement) {
			SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
			whereExpr = deleteStmt.getWhere();
			deleteStmt.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
			return deleteStmt.toString();
		} else {
			return originalSql + " AND 1 = 0";
		}
	}

	//处理排除列
	private String doHandleExceptColumns(String originalSql, List<ColumnPermWrap> exceptColumns,  String tableColumnInject) {
		if (Checker.beEmpty(exceptColumns)) {
			return originalSql;
		}

		SQLStatement stmt = buildSQLStatement(originalSql, JdbcUtils.MYSQL);
		if (stmt instanceof SQLSelectStatement) {
			SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
			SQLSelect sqlselect = selectStmt.getSelect();
			SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

			if (Checker.beNotEmpty(tableColumnInject)) {
				TableSelectQueryVisitor visitor = new TableSelectQueryVisitor();
				selectStmt.accept(visitor);
				MySqlSelectQueryBlock targetSelect = visitor.getAliasMap().get(tableColumnInject);
				doExceptColumns(targetSelect, exceptColumns);
			} else {
				doExceptColumns(query, exceptColumns);
			}
			return sqlselect.toString();

		}
		return originalSql;

	}

	//追加SQL
	private String contractSql(String originalSql, String additionalSql, boolean beOr,  String tableNameInject) {
		SQLStatement stmt = buildSQLStatement(originalSql, JdbcUtils.MYSQL);
		SQLExpr constraintsExpr = buildSQLExpr(additionalSql, JdbcUtils.MYSQL);
		SQLExpr whereExpr;

		if (stmt instanceof SQLSelectStatement) {
			SQLSelectStatement selectStmt = (SQLSelectStatement) stmt;
			SQLSelect sqlselect = selectStmt.getSelect();
			SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

			if (Checker.beNotEmpty(tableNameInject)) {
				TableSelectQueryVisitor visitor = new TableSelectQueryVisitor();
				selectStmt.accept(visitor);
				MySqlSelectQueryBlock targetSelect = visitor.getAliasMap().get(tableNameInject);
				if (Checker.beNotNull(targetSelect)) {
					whereExpr = targetSelect.getWhere();
					targetSelect.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
				} else {
					SQLExpr noEqualExpr = buildSQLExpr(" 1 = 0 ", JdbcUtils.MYSQL);
					whereExpr = query.getWhere();
					query.setWhere(beOr ? buildOrWhere(whereExpr, noEqualExpr) : buildAndWhere(whereExpr, noEqualExpr));
				}
//				doExceptColumns(targetSelect, exceptColumns);
			} else {
//				doExceptColumns(query, exceptColumns);
				whereExpr = query.getWhere();
				query.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
				sqlselect.setQuery(query);
			}

			return sqlselect.toString();
			//update delete 咱不支持tableNameInject层面的处理
		} else if (stmt instanceof SQLUpdateStatement) {
			SQLUpdateStatement updateStmt = (SQLUpdateStatement) stmt;
			whereExpr = updateStmt.getWhere();
			updateStmt.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
			return updateStmt.toString();
		} else if (stmt instanceof SQLDeleteStatement) {
			SQLDeleteStatement deleteStmt = (SQLDeleteStatement) stmt;
			whereExpr = deleteStmt.getWhere();
			deleteStmt.setWhere(beOr ? buildOrWhere(whereExpr, constraintsExpr) : buildAndWhere(whereExpr, constraintsExpr));
			return deleteStmt.toString();
		} else {
			return originalSql + " AND 1 = 0";
		}
	}


	@Override
	public Object plugin(Object target) {
		return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
	}

}
