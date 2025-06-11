
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
