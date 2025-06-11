package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * DataPermSearchWrap
 *
 * @author zening 2023/11/7 14:52
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class DataPermSearchWrap {

    //表名
    private String tableName;
    //表别名
    private String aliasName;
//    //SQL追加模式（如果有已有相同列的查询条件）
//    private String conditionAppendMode;
    //SQL条件中对应的表重命名
    private String conditionTableAlias;
//    //SQL条件追加到表格名称对应的层测
//    private String conditionAppendWithTable;

    private List<AdvanceSearchWrap> permExpresses;
}
