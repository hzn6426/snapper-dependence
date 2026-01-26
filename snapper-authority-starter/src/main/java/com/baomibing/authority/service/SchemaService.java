
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

package com.baomibing.authority.service;




import com.baomibing.core.common.SearchResult;
import com.baomibing.orm.perm.ActionTableColumn;
import com.baomibing.orm.perm.Table;
import com.baomibing.orm.perm.TableColumn;

import java.util.List;
import java.util.Set;

public interface SchemaService {

    /**
     * 根据表格获取对应的列信息
     * @param tables
     * @param beFresh
     * @return
     */
    List<ActionTableColumn> fetchColumnByTable(Set<String> tables, boolean beFresh);


    /**
     * 根据表格获取对应的列信息(关联数据权限列)
     */
    SearchResult<ActionTableColumn> fetchColumnByTableWithPerm(TableColumn column, int pageNo, int pageSize);

    /**
     * 获取所有表格
     * @return
     */
    SearchResult<Table> fetchTables(Table table, int pageNo, int pageSize);

    /**
     * 根据表名获取表格列表
     * @param tableNames
     * @return
     */
    List<Table> fetTableByNames(Set<String> tableNames);
}
