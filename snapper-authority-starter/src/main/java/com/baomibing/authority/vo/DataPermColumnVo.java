package com.baomibing.authority.vo;

import com.baomibing.orm.perm.ActionTableColumn;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * DataPermColumnVo
 *
 * @author frog 2025/1/13 10:37
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class DataPermColumnVo {

    private String tableComment;

    private String table;

    private String alias;

    private List<ActionTableColumn> columns;
}
