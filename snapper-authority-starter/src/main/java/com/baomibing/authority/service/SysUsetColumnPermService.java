
package com.baomibing.authority.service;



import com.baomibing.authority.dto.UsetColumnPermDto;

import java.util.List;
import java.util.Set;

public interface SysUsetColumnPermService {

    void saveColumnPerm(UsetColumnPermDto perm);

    UsetColumnPermDto getUsetColumnPerm(String usetId, String permId);

    List<UsetColumnPermDto> listUsetColumnPerm(Set<String> usetIds, String permId);

    void deleteUsetColumnPerm(String usetId, String permId);
}
