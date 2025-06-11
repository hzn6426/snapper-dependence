
package com.baomibing.authority.service;


import com.baomibing.authority.dto.UsetDto;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.wrap.CommonTreeWrap;

import java.util.List;
import java.util.Set;

public interface SysUsetService extends MBaseService<UsetDto> {

    SearchResult<UsetDto> search(UsetDto v, int pageNumber, int pageSize);
    
    /**
     * 启用
     *
     * @param ids 用户组ID列表
     */
    void use(Set<String> ids);
    
    /**
     * 停用
     *
     * @param ids 用户组ID列表
     */
    void stop(Set<String> ids);
    
    /**
     * 删除
     *
     * @param ids 用户组ID列表
     */
    void deleteUsets(Set<String> ids);

    /**
     * 获取所有用户组列表
     * @return
     */
    List<CommonTreeWrap> treeAllUset();

    void saveUset(UsetDto uset);

    void updateUset(UsetDto uset);


}
