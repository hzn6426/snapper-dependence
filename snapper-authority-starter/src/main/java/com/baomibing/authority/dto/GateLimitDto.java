package com.baomibing.authority.dto;

import com.baomibing.authority.action.LimitAction;
import com.baomibing.authority.state.LimitState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

import static com.baomibing.authority.action.LimitAction.stop;
import static com.baomibing.authority.action.LimitAction.use;
import static com.baomibing.authority.state.LimitState.STOPPED;
import static com.baomibing.authority.state.LimitState.USED;

/**
 * GateLimitDto
 *
 * @author zening
 * @version 1.0.0
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class GateLimitDto extends StateProcess<LimitState, LimitAction> {

    private String id;
    private String name;
    private Integer durationInSecond;
    private Integer allowVolume;
    private Integer speedInSecond;
    private String ruleTuser;
    private String ruleUser;
    private String ruleSystemTag;
    private String ruleUserTag;
    private String ruleUrl;
    private String state;
    private String groupId;
    private Boolean beLock;

    private String uuid;

    private List<GateLimitStagDto> stags;
    private List<GateLimitUtagDto> utags;
    private List<GateLimitUserDto> users;
    private List<GateLimitRequestDto> requests;
    private List<GateLimitTuserDto> tusers;

    @Override
    public SProcess<LimitState, LimitAction> initProcess() {
        return getBuilder().initState(STOPPED).deleteable(Sets.newHashSet(STOPPED))
                .process()
                .source(STOPPED).target(USED).action(use)
                .and()
                .process()
                .source(USED).target(STOPPED).action(stop)
                .build();
    }
}
