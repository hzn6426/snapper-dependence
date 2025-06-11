package com.baomibing.authority.vo;

import com.baomibing.authority.constant.enums.BusinessPermScopeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

/**
 * DataPermFunctionVo
 *
 * @author frog 2025/1/9 16:36
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class DataPermFunctionVo {
    private String permScope = BusinessPermScopeEnum.CURRENT_USER.name();
    private Date permStartTime;
    private Date permEndTime;
    private Set<String> entrustIds;
    private Set<String> exceptEntrustIds;
}
