/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.web.common;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * WEB工具类
 * 
 * @author zening
 * @since 1.0.0
 */
@Slf4j
public abstract class WebHelper {

	private static final int BYTES_ONCE_READ = 1024 * 512;// 读取缓存 0.5M
	private static final int ONE_MB = BYTES_ONCE_READ * 2;
	private static final int ONE_POINT_FIVE_MB = BYTES_ONCE_READ * 3;
	private static final int DOUBLE_MB = BYTES_ONCE_READ * 4;
	private static final int TEN_MB = 1024 * 1024 * 10;
	private static final int FIFTY_MB = 1024 * 1024 * 50;
	private static final int HUNDRED_MB = 1024 * 1024 * 100;

	private static int cacBuffer(long fileSize) {
		int bytes_once_read = BYTES_ONCE_READ;// 默认0.5M读一次
		if (fileSize > HUNDRED_MB) {
			bytes_once_read = DOUBLE_MB;
		} else if (fileSize > FIFTY_MB) {
			bytes_once_read = ONE_POINT_FIVE_MB;
		} else if (fileSize > TEN_MB) {
			bytes_once_read = ONE_MB;
		}
		int read = (int) Math.min(bytes_once_read, fileSize);
		return read;
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		String contentType = Optional.fromNullable(request.getHeader("Content-Type")).or("");
		String accept = Optional.fromNullable(request.getHeader("Accept")).or("");
        return "XMLHttpRequest".equals(header) || contentType.toLowerCase().contains("application/json")
|| accept.toLowerCase().contains("application/json");
	}

	public static boolean isAjaxRequest(HttpHeaders header) {
		List<String> headerList = header.get("X-Requested-With");
		if (CollectionUtils.isEmpty(headerList)) {
			return false;
		}
		MediaType mediaType = header.getContentType();
		boolean beJson = false;
		List<MediaType> acceptList = header.getAccept();
		if (!CollectionUtils.isEmpty(acceptList)) {
			for (MediaType aceept : acceptList) {
				if (aceept.includes(MediaType.APPLICATION_JSON)) {
					beJson = true;
					break;
				}
			}
		}
        return "XMLHttpRequest".equals(headerList.get(0)) || mediaType.includes(MediaType.APPLICATION_JSON) || beJson;
	}

	public static boolean isAjaxRequest(WebRequest request) {
		String header = request.getHeader("X-Requested-With");
		String contentType = Optional.fromNullable(request.getHeader("Content-Type")).or("");
		String accept = Optional.fromNullable(request.getHeader("Accept")).or("");
        return "XMLHttpRequest".equals(header) || contentType.toLowerCase().contains("application/json")
			|| accept.toLowerCase().contains("application/json");
	}

	public static void write(HttpServletResponse response, Object object) {
		if (response == null) {
			throw new NullPointerException("response can not be null.");
		}

		response.setCharacterEncoding("UTF-8");
		try {
			String res = ClassUtils.isPrimitiveOrWrapper(object.getClass()) ? object.toString()
					: JSONObject.toJSONString(object);
			response.getWriter().write(new String(res.getBytes(), StandardCharsets.UTF_8));
		} catch (IOException e) {
			log.error(Throwables.getStackTraceAsString(e));
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String getLocale(HttpServletRequest request) {
		WebApplicationContext context = getWebApplicationContext(request);
		if (context == null)
			return null;
		SessionLocaleResolver localeResolver = (SessionLocaleResolver) context.getBean("localeSessionResolver");
		Locale locale = localeResolver.resolveLocale(request);
		return locale.toString();
	}

	public static void bindLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		WebApplicationContext context = getWebApplicationContext(request);
		SessionLocaleResolver localeResolver = (SessionLocaleResolver) context.getBean("localeSessionResolver");
		if (locale != null) {
			localeResolver.setLocale(request, response, locale);
		}
	}

	public static WebApplicationContext getWebApplicationContext(HttpServletRequest request) {
		WebApplicationContext context = RequestContextUtils.findWebApplicationContext(request);
		if (context == null)
			return null;
		return context;
	}

	/**
	 * 根据request读取post内容
	 * <p>
	 * 注意需要封装request成
	 * {@link MultiReadHttpServletRequest},否则只能读取一次
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static String receivePost(HttpServletRequest request) {
		// 读取请求内容
		String params = "";
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			// 将资料解码
            // params = URLDecoder.decode(reqBody, StandardCharsets.UTF_8.toString());
			return sb.toString();
		} catch (IOException e) {
			log.error(Throwables.getStackTraceAsString(e));
			throw new RuntimeException("read request inputstream error!");
		}

	}

	/**
	 * 获取访问者IP
	 * 
	 * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
	 * 
	 * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)， 如果还不存在则调用Request
	 * .getRemoteAddr()。
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip) && StringUtils.contains(ip, ",")) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			ip = StringUtils.substringBefore(ip, ",");
		}
		// 处理localhost访问
		if (StringUtils.isBlank(ip) || "unkown".equalsIgnoreCase(ip) || StringUtils.split(ip, ".").length != 4) {
			try {
				InetAddress inetAddress = InetAddress.getLocalHost();
				ip = inetAddress.getHostAddress();
			} catch (UnknownHostException e) {
				log.error(Throwables.getStackTraceAsString(e));
			}
		}
		return ip;
	}

	// 获取请求的域名
	public static String getDomain(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		if (StringUtils.isNotEmpty(contextPath)) {
			return contextPath;
		} else {
			if (request.getServerPort() == 80 || request.getServerPort() == 443) {
				return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
			} else {
				return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ request.getContextPath();
			}
		}
	}

	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	 * 下载文件
	 * 
	 * @param fileName 文件路径
	 * @param in
	 * @param response
	 * @throws IOException
	 */
	public static void downLoadFile(String fileName, InputStream in, HttpServletResponse response) throws IOException {
		Validate.notNull(fileName, "filePath not be null!");
		Validate.notNull(in, "download file exception, inputstream not be null!");
		Validate.notNull(response, "httpServletResponse not be null.");
		int buffer = cacBuffer(in.available());

		try (OutputStream out = response.getOutputStream();
				BufferedInputStream br = new BufferedInputStream(in);
				BufferedOutputStream bos = new BufferedOutputStream(out)) {
//			String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
			response.setContentType("application/octet-stream");// x-msdownload
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
			// 读取数据
			// 一次性取多少字节
			byte[] bytes = new byte[buffer];
			// 接受读取的内容(n就代表的相关数据，只不过是数字的形式)
			int bytesRead;
			// 循环取出数据
			while ((bytesRead = br.read(bytes)) != -1) {
				// 写入相关文件
				bos.write(bytes, 0, bytesRead);
			}
			out.flush();
		} finally {
            in.close();
        }

	}

	/**
	 * 下载文件
	 * 
	 * @param fileName 文件路径
	 * @param in
	 * @param response
	 * @throws IOException
	 */
	public static void downLoadImage(String fileName, InputStream in, HttpServletResponse response) throws IOException {
		Validate.notNull(fileName, "filePath not be null!");
		Validate.notNull(in, "download file exception, inputstream not be null!");
		Validate.notNull(response, "httpServletResponse not be null.");
		int buffer = cacBuffer(in.available());

		try (OutputStream out = response.getOutputStream();
				BufferedInputStream br = new BufferedInputStream(in);
				BufferedOutputStream bos = new BufferedOutputStream(out)) {
//			String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
			response.setContentType("image/png");
			response.setHeader("Content-Disposition",
					"inline;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
			// 读取数据
			// 一次性取多少字节
			byte[] bytes = new byte[buffer];
			// 接受读取的内容(n就代表的相关数据，只不过是数字的形式)
			int bytesRead;
			// 循环取出数据
			while ((bytesRead = br.read(bytes)) != -1) {
				// 写入相关文件
				bos.write(bytes, 0, bytesRead);
			}
			out.flush();
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}
}
