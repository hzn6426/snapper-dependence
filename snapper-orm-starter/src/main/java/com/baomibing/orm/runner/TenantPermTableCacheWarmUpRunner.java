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
package com.baomibing.orm.runner;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.core.annotation.Action;
import com.baomibing.core.annotation.TenantAction;
import com.baomibing.core.annotation.TenantActionConnect;
import com.baomibing.orm.perm.*;
import com.baomibing.tool.constant.Strings;
import com.baomibing.tool.constant.TenantRedisKeyConstant;
import com.baomibing.tool.util.Checker;
import com.baomibing.tool.util.JarUtil;
import com.baomibing.tool.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PermTableCacheWarmUpRunner
 *
 * @author zening 2023/4/23 11:22
 * @version 1.0.0
 **/
//@Component
@Slf4j
public class TenantPermTableCacheWarmUpRunner implements CommandLineRunner {

    @Value("${spring.application.name}") private String serviceName;
    @Value("${snapper.taction.package:}") private String actionPackage;
    @Autowired private List<SqlSessionFactory> sqlSessionFactoryList;
    @Autowired private CacheService cacheService;

    private Object initValue(Class<?> type) {
        if (Boolean.class.isAssignableFrom(type)) {
            return Boolean.FALSE;
        } else if (Number.class.isAssignableFrom(type)) {
            return -1;
        } else if (String.class.isAssignableFrom(type)) {
            return "-1";
        } else if (Date.class.isAssignableFrom(type)) {
            return new Date();
        } else if (Collection.class.isAssignableFrom(type)) {
            return Lists.newArrayList("-1");
        }
        return "";
    }

    private ActionMapper getActionMapper(Configuration configuration, String action, String mapper) throws IllegalAccessException, InstantiationException {
        Map<String, Object> paramMap = Maps.newHashMap();
        MappedStatement mappedStatement = configuration.getMappedStatement(mapper);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        if ((sqlSource instanceof DynamicSqlSource)) {

            boolean beUpdateById = false;
            if (mapper.contains("update")) {
                String MAPPER_UPDATE_BY_ID = "updateById";
                beUpdateById = mapper.contains(MAPPER_UPDATE_BY_ID);
                Class<?> entityClass = getMapperEntityClass(actionMethodClassMap.get(action));
                if (Checker.beNotNull(entityClass)) {
                    paramMap.put("et", entityClass.newInstance());
                }
            }

            DynamicSqlSource dynamicSqlSource = (DynamicSqlSource) sqlSource;
            MixedSqlNode rootSqlNode = (MixedSqlNode) FieldUtils.readField(dynamicSqlSource, "rootSqlNode", true);
            List<SqlNode> contents = (List<SqlNode>) FieldUtils.readField(rootSqlNode, "contents", true);
            for (SqlNode node : contents) {
                if (node instanceof ForEachSqlNode) {
                    ForEachSqlNode forEachSqlNode = (ForEachSqlNode) node;
                    String collectionExpression = (String) FieldUtils.readField(forEachSqlNode, "collectionExpression", true);
                    paramMap.put(collectionExpression, Lists.newArrayList("-1"));
                } else if (node instanceof TextSqlNode) {
                    TextSqlNode textSqlNode = (TextSqlNode) node;
                    String text = (String) FieldUtils.readField(textSqlNode, "text", true);
                    Pattern p = Pattern.compile("#\\{([^}]+)");
                    Matcher m = p.matcher(text);
                    //获取 #{} 中的内容
                    while (m.find()) {
                        paramMap.put(m.group(1), "");
                    }
                } else if (node instanceof SetSqlNode) {
                    SetSqlNode setSqlNode = (SetSqlNode) node;
                    MixedSqlNode mixedSqlNode = (MixedSqlNode) FieldUtils.readField(setSqlNode, "contents", true);
                    List<SqlNode> nodes = (List<SqlNode>) FieldUtils.readField(mixedSqlNode, "contents", true);
                    for (SqlNode nestedNode : nodes) {
                        if (nestedNode instanceof  IfSqlNode) {
                            IfSqlNode ifSqlNode = (IfSqlNode) nestedNode;
    //                            ifSqlNode.
                            String test = (String)FieldUtils.readField(ifSqlNode, "test", true);
                            String IF_NODE_TEST_CONTAINS = " != null";
                            int index = test.indexOf(IF_NODE_TEST_CONTAINS);
                            if (index != -1) {
                                String left = test.substring(0, index);
                                String field = left.substring(left.indexOf("['") + 2, left.lastIndexOf("']"));

                                if (Checker.beNotEmpty(field) && Checker.beNotNull(paramMap.get("et"))) {
                                    Field privateField = FieldUtils.getField(paramMap.get("et").getClass(), field, true);
                                    FieldUtils.writeDeclaredField(paramMap.get("et"), field, initValue(privateField.getType()), true);
                                    if (beUpdateById) {
                                        break;
                                    }
                                }
                            }
                        } else if (nestedNode instanceof TextSqlNode) {
                            TextSqlNode textSqlNode = (TextSqlNode) nestedNode;
                            String text = (String) FieldUtils.readField(textSqlNode, "text", true);
                            Pattern p = Pattern.compile("#\\{([^}]+)");
                            Matcher m = p.matcher(text);
                            //获取 #{} 中的内容
                            while (m.find()) {
                                paramMap.put(m.group(1), "");
                            }
                        }
                    }
                }
            }
        }

        //解析SQL
        String sql = sqlSource.getBoundSql(paramMap).getSql();
        sql = sql.replaceAll("\\?", "-1");
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement stmt = stmtList.get(0);

        //获取表名
        TableAliasVisitor visitor = new TableAliasVisitor();
        String tableNameInject = actionTableAliasMap.get(action);

        if (Checker.beNotEmpty(tableNameInject)) {
            TableSelectQueryVisitor selectVisitor = new TableSelectQueryVisitor();
            stmt.accept(selectVisitor);
            MySqlSelectQueryBlock selectQuery = selectVisitor.getAliasMap().get(tableNameInject);
            selectQuery.accept(visitor);
        } else {
            stmt.accept(visitor);
        }
        Map<String, String> aliasMap = visitor.getTableAliasMap();

        Map<String, String> alias4ColumnMap = visitor.getTableAliasMap();

        Map<String, Set<String>> columnMap = visitor.getTableColumnMap();

        String columnNameInject = actionColumnAliasMap.get(action);
        if (Checker.beNotEmpty(columnNameInject) && !columnNameInject.equals(tableNameInject)) {
            TableAliasVisitor columnVisitor = new TableAliasVisitor();
            TableSelectQueryVisitor selectVisitor = new TableSelectQueryVisitor();
            stmt.accept(selectVisitor);
            MySqlSelectQueryBlock selectQuery = selectVisitor.getAliasMap().get(columnNameInject);
            if (Checker.beNotNull(selectQuery)) {
                selectQuery.accept(columnVisitor);
                columnMap = columnVisitor.getTableColumnMap();
                alias4ColumnMap = columnVisitor.getTableAliasMap();
//                Map<String, String> columnAliasMap = columnVisitor.getTableAliasMap();
//
//                Map<String, String> tableMap = MapUtils.invertMap(aliasMap);
//                Map<String, Set<String>> columnTempMap = Maps.newHashMap();
//                for (Map.Entry<String, Set<String>> entry : columnMap.entrySet()) {
//                    String tableName = columnAliasMap.get(entry.getKey());
//                    if (Checker.BeNotNull(tableMap.get(tableName))) {
//                        columnTempMap.put(tableMap.get(tableName), entry.getValue());
//                    }
//                }
//                columnMap = columnTempMap;
            }

        }



        ActionMapper am = new ActionMapper();
        am.setMapper(mapper).setAction(action);
        List<ActionWhereTable> tables = Lists.newArrayList();
        for (Map.Entry<String, String> alias : aliasMap.entrySet()) {
            ActionWhereTable at = new ActionWhereTable();
            at.setTable(alias.getValue()).setAlias(alias.getKey());
            tables.add(at);
        }
        am.setWhereTables(tables);

        //筛选 保留同一个表中列最多的
        Map<String, Integer> tableColumnNumberMap = Maps.newHashMap();
        Map<String, String> tableNameMap = Maps.newHashMap();

        for (Map.Entry<String, Set<String>> entry : columnMap.entrySet()) {
            String tableName = alias4ColumnMap.get(entry.getKey());
            Integer num = tableColumnNumberMap.get(tableName);
            if (Checker.beNull(num)) {
                tableColumnNumberMap.put(tableName, entry.getValue().size());
                tableNameMap.put(tableName, entry.getKey());
            } else {
                if (num < entry.getValue().size()) {
                    tableColumnNumberMap.put(tableName, entry.getValue().size());
                    tableNameMap.put(tableName, entry.getKey());
                }
            }
        }

        Map<String, Set<String>> targetColumnMap = Maps.newHashMap();
        List<String> targetTableAlias = Lists.newArrayList(tableNameMap.values());
        for (Map.Entry<String, Set<String>> entry : columnMap.entrySet()) {
            if (targetTableAlias.contains(entry.getKey())) {
                targetColumnMap.put(entry.getKey(), entry.getValue());
            }
        }

        List<ActionSelectTable> selectTables = Lists.newArrayList();
        for (Map.Entry<String, Set<String>> entry : targetColumnMap.entrySet()) {
            Set<String> columns = entry.getValue();
            ActionSelectTable ast = new ActionSelectTable();
            ast.setTable(alias4ColumnMap.get(entry.getKey())).setAlias(entry.getKey());
            List<ActionTableColumn> tableColumns = Lists.newArrayList();
            columns.forEach( c -> {
                ActionTableColumn tc = new ActionTableColumn();
                tc.setColumnName(c);
                tc.setTableName(ast.getTable());
                tableColumns.add(tc);
            });
            ast.setColumns(tableColumns);
            selectTables.add(ast);
        }
        am.setSelectTables(selectTables);

        //处理特殊列
        Set<String> specialColumns = visitor.getSpecialColumns();
        actionColumnMap.put(action, specialColumns);
        return am;
    }

    private ActionMapper merge(List<ActionMapper> mapperList) {

        if (Checker.beEmpty(mapperList)) {
            return null;
        }
//        Map<String, Set<TableColumn>> tableColumnMap = Maps.newHashMap();
        List<ActionWhereTable> conditionTables = Lists.newArrayList();
        List<ActionSelectTable> selectTables = Lists.newArrayList();
        String mapper = null,  action = null;
        for (ActionMapper am : mapperList) {
            if (Checker.beEmpty(conditionTables) || am.getSelectTables().size() > selectTables.size()) {
                conditionTables = am.getWhereTables();
                mapper = am.getMapper();
                action = am.getAction();
                selectTables = am.getSelectTables();
            }
        }
        ActionMapper result = new ActionMapper();
        result.setMapper(mapper).setAction(action);
        result.setWhereTables(conditionTables).setSelectTables(selectTables);
        return result;
    }

    private final Map<String, String> actionTableAliasMap = Maps.newHashMap();
    private final Map<String, String> actionColumnAliasMap = Maps.newHashMap();
    private final Map<String, Set<String>> actionColumnMap = Maps.newHashMap();
    private final Map<String, Class<?>> actionMethodClassMap = Maps.newHashMap();

    private URLClassLoader loadAuthorityClassLoader() {
        try {
            String jarPath = JarUtil.getJarFilePath(Action.class);
            final String jarName = "snapper-core-starter";
            final String targetJarName = "snapper-authority-starter.jar!/";
            int index = jarPath.indexOf(jarName);
            String path = jarPath.substring(0, index) + targetJarName;
            URL url = new URL("jar:file:" + path);
            URL[] urls = new URL[1];
            urls[0] = url;
            return URLClassLoader.newInstance(urls);
        } catch (MalformedURLException e) {
            //ignore
        }
        return null;
    }
    private static final String AUTHORITY_PACKAGE = "com.baomibing.authority";
    private boolean beAuthorityPackage() {
        return AUTHORITY_PACKAGE.equals(actionPackage);
    }
    @Override
    public void run(String... args) throws Exception {
        try {
            if (Checker.beEmpty(actionPackage)) {
                return;
            }

            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .addClassLoaders()
                    .forPackages(actionPackage.split(Strings.COMMA))
                    .filterInputsBy(new FilterBuilder().excludePackage(AUTHORITY_PACKAGE))
                    .setScanners(Scanners.MethodsAnnotated);
//            if (beAuthorityPackage()) {
//                builder.addClassLoaders(loadAuthorityClassLoader());
//            }
            //获取所有Action Connection
            Reflections reflections = new Reflections(builder);
            Set<Method> methods =
                    reflections.get(Scanners.MethodsAnnotated.with(TenantAction.class).as(Method.class));

            Map<String, List<String>> actionMapperMap = Maps.newHashMap();


            List<ActionMapper> actions = Lists.newArrayList();
            for(Method method : methods) {
                TenantAction action = method.getAnnotation(TenantAction.class);
                TenantActionConnect actionConnect = method.getAnnotation(TenantActionConnect.class);
                Class<?> clazz = method.getDeclaringClass();
                actionMethodClassMap.put(action.value(), clazz);
                String clazzName = getMapperClassName(clazz);
                if (Checker.beNotEmpty(actionConnect.value())) {
                    String[] mapperNames = actionConnect.value();
                    List<String> mapperList = Lists.newArrayList();
                    for (String m : mapperNames) {
                        String mapper = clazzName + Strings.DOT + m;
                        mapperList.add(mapper);
                    }
                    actionMapperMap.put(action.value(), mapperList);
                    actionTableAliasMap.put(action.value(), actionConnect.tableNameWithAuthInject());
                    actionColumnAliasMap.put(action.value(), ObjectUtil.defaultIfNull(actionConnect.tableNameWithColumnInject(),actionConnect.tableNameWithAuthInject()));
                }

            }

            for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
                Configuration configuration = sqlSessionFactory.getConfiguration();
                for (Map.Entry<String, List<String>> actionEntry : actionMapperMap.entrySet()) {
                    List<String> mappers = actionEntry.getValue();
                    if (Checker.beEmpty(mappers)) {
                        continue;
                    }
                    ActionMapper am = null;
                    if (mappers.size() > 1) {
                        List<ActionMapper> actionMaps = Lists.newArrayList();
                        for (String mapper : mappers) {
                            ActionMapper actionMapper = getActionMapper(configuration, actionEntry.getKey(), mapper);
                            actionMaps.add(actionMapper);
                        }
                        ActionMapper target = merge(actionMaps);
                        if (Checker.beNotNull(target)) {
                            am = target;
                        }

                    } else {
                        am = getActionMapper(configuration, actionEntry.getKey(), mappers.get(0));
                    }
                    if (Checker.beNotNull(am) ) {
                        List<ActionSelectTable> selectTables = am.getSelectTables();
                        if (Checker.beNotEmpty(selectTables)) {
                            ActionSelectTable ast = selectTables.get(0);
                            Set<String> specialColumns = actionColumnMap.get(actionEntry.getKey());
                            if (Checker.beNull(ast.getColumns())) {
                                ast.setColumns(Lists.newArrayList());
                            }
                            if(Checker.beNotEmpty(specialColumns)) {
                                for (String special : specialColumns) {
                                    ActionTableColumn tc = new ActionTableColumn();
                                    tc.setBeSpecial(Boolean.TRUE);
                                    tc.setTableName(ast.getTable());
                                    tc.setColumnComment("别名列");
                                    tc.setColumnName(special);
                                    ast.getColumns().add(tc);
                                }
                            }
                        }
                        actions.add(am);
                    }
                }
            }
            String cacheActionConnectKey = TenantRedisKeyConstant.CACHE_ACTION_CONNECT_KEY_PREFIX + serviceName;
            List<String> keys = cacheService.lRange(cacheActionConnectKey, 0 ,-1);
            if (Checker.beNotEmpty(keys)) {
                cacheService.del(keys);
            }
            actions.forEach(a -> {
                String key = TenantRedisKeyConstant.CACHE_ACTION_CONNECT_PREFIX + a.getAction();
                cacheService.set(key, JSONObject.toJSONString(a));
                cacheService.lPush(cacheActionConnectKey, key);
            });

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private String getMapperClassName(Class<?> methodClass) {
        String result = Strings.EMPTY;
        if (Checker.beNull(methodClass) || Checker.beNull(methodClass.getSuperclass()))
            return result;
        Class<?> superClass = methodClass.getSuperclass();
        if (!superClass.getName().contains("MBaseServiceImpl")) {
            return result;
        }
        try {
            Method method = superClass.getDeclaredMethod("reflectForMapperClass");
            method.setAccessible(true);
            Object classAInstance = methodClass.newInstance();
            result = method.invoke(classAInstance).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    private Class<?> getMapperEntityClass(Class<?> methodClass) {
        Class<?> result = null;
        if (Checker.beNull(methodClass) || Checker.beNull(methodClass.getSuperclass()))
            return null;
        Class<?> superClass = methodClass.getSuperclass();
        if (!superClass.getName().contains("MBaseServiceImpl")) {
            return null;
        }
        try {
            Method method = superClass.getDeclaredMethod("reflectForEntityClass");
            method.setAccessible(true);
            Object classAInstance = methodClass.newInstance();
            result = (Class<?>) method.invoke(classAInstance);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
