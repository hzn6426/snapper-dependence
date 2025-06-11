package com.baomibing.authority.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * DataPermBusinessVo
 *
 * @author frog 2025/1/10 16:26
 * @version 1.0.0
 **/
@Data @Accessors(chain = true)
public class DataPermBusinessVo {

    private Date permStartTime;

    private Date permEndTime;

    private String permExpress;

    private Boolean beOrCondition;

    private Boolean beForceCondition;

    private List<AdvanceSearchVo> searchExpresses;
}

