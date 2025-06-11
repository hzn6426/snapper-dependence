
package com.baomibing.authority.listener;


import com.baomibing.authority.dto.UserLogDto;
import com.baomibing.authority.service.SysUserLogService;
import com.baomibing.tool.util.URLUtil;
import com.baomibing.web.event.UserLogEvent;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * 用户日志监听，监听日志，写入日志
 * 
 * @author zening
 * @since 1.0.0
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class ULogEventListener {

	@Autowired private SysUserLogService userLogService;
	@Autowired private Mapper mapper;

    @Async
    @EventListener
    public void handleUserLogEvent(UserLogEvent event) {
        if (event == null) {
            return;
        }
        String cuserCnName = event.getCreateUserCnName();
        String uuserCnName = event.getUpdateUserCnName();
        cuserCnName = URLUtil.decode(URLUtil.decode(cuserCnName));
        uuserCnName = URLUtil.decode(URLUtil.decode(uuserCnName));
        UserLogDto ul = mapper.map(event, UserLogDto.class);
        ul.setCreateUserCnName(cuserCnName).setUpdateUserCnName(uuserCnName);
        userLogService.doSaveLogAsync(ul);
    }
}
