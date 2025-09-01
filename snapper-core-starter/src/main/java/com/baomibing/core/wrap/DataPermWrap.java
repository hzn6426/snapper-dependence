/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.wrap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * DataPermWrap
 *
 * @author zening 2023/5/18 09:15
 * @version 1.0.0
 **/
@Data
@Accessors(chain = true)
public class DataPermWrap {

    //原始条件 有相同列时，是否强制追加相同列查询
//    private Boolean beForceCondition = Boolean.FALSE;

    private Boolean beOrCondition = Boolean.FALSE;

    private List<DataPermSearchWrap> searchWraps;

//    private List<AdvanceSearchWrap> permExpresses;
}
