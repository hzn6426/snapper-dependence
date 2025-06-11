
package com.baomibing.authority.service;



import com.baomibing.authority.dto.DataPermColumnDto;

import java.util.List;
import java.util.Set;

public interface SysDataPermColumnService {

    void saveColumns(List<DataPermColumnDto> columns);

    void deleteByTable(String tableName);

    void deleteColumns(Set<String> ids);

    /**
     * 根据表名获取数据权限对应枚举列
     * @param tableName
     * @return
     */
    List<DataPermColumnDto> listColumnsByTable(String tableName);

    List<DataPermColumnDto> listColumnsByTables(Set<String> tableNames);
}
