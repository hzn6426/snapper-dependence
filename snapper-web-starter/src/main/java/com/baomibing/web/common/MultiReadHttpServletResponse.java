/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
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
