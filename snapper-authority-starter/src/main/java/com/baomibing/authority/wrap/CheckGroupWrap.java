/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.wrap;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
@JsonInclude(Include.NON_NULL)
@Data @Accessors(chain = true)
@AllArgsConstructor
public class CheckGroupWrap implements Serializable {
	private static final long serialVersionUID = 776038945770051308L;

	private String label;
	
	private String value;
}
