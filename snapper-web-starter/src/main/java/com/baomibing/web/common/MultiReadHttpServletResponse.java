/*
 * Copyright (c) 2020-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.web.common;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.mock.web.DelegatingServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 封装Response(解决httpResponse getOutputStram只能读取一次的问题)
 * 
 * @author zening
 * @since 1.0.0
 */
public class MultiReadHttpServletResponse extends HttpServletResponseWrapper implements Serializable {

	private static final long serialVersionUID = -1L;

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	final PrintStream ps = new PrintStream(baos);

	public MultiReadHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), ps));
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), ps)));
	}

	public String getResponseAsString() {
		try {
			return baos.toString(StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
		return "";
	}
}
