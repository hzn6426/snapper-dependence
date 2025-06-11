
package com.baomibing.authority.dto;

import com.baomibing.authority.action.DictionaryAction;
import com.baomibing.authority.constant.enums.DictionaryTypeEnum;
import com.baomibing.authority.state.DictionaryState;
import com.baomibing.core.process.SProcess;
import com.baomibing.core.process.StateProcess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.EnumSet;

import static com.baomibing.authority.action.DictionaryAction.STOP;
import static com.baomibing.authority.action.DictionaryAction.USE;
import static com.baomibing.authority.state.DictionaryState.ACTIVE;
import static com.baomibing.authority.state.DictionaryState.STOPPED;

/**
 * 系统字典
 * 
 * @author zening
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DictionaryDto extends StateProcess<DictionaryState, DictionaryAction> {
	private static final long serialVersionUID = 6014243482390899479L;

	private String id;

    private String dictCode;

    private String dictName;
    private DictionaryTypeEnum dictType;
    private Integer priority;
    private String state;
    private Boolean beDelete;
    private String groupId;
    private Boolean beLock;


    private String childDictCode;
    private String childDictName;


    @Override
	public SProcess<DictionaryState, DictionaryAction> initProcess() {
		return builder
				.initState(ACTIVE).editable(EnumSet.of(ACTIVE, STOPPED)).deleteable(EnumSet.of(ACTIVE, STOPPED))
				.process().source(STOPPED).target(ACTIVE).action(USE)
                .and()
				.process().source(ACTIVE).target(STOPPED).action(STOP)
                .build();
    }
}
