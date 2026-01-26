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
package com.baomibing.tool.util;

import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * 文件工具
 * 
 * @author zening
 * @since 1.0.0
 */
public abstract class FileUtil {
	private static final int BYTES_ONCE_READ = 1024 * 512;// 读取缓存 0.5M
	private static final int ONE_MB = BYTES_ONCE_READ * 2;
	private static final int ONE_POINT_FIVE_MB = BYTES_ONCE_READ * 3;
	private static final int DOUBLE_MB = BYTES_ONCE_READ * 4;
	private static final int TEN_MB = 1024 * 1024 * 10;
	private static final int FIFTY_MB = 1024 * 1024 * 50;
	private static final int HUNDRED_MB = 1024 * 1024 * 100;

	public static int cacBuffer(long fileSize) {
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

	/**
	 * 写文件
	 * 
	 * @param filePath 文件路径（包括文件名称）
	 * @param is       文件输入流
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeFile(String filePath, InputStream is) throws FileNotFoundException, IOException {
		int buffer = cacBuffer(is.available());
		try (BufferedInputStream br = new BufferedInputStream(is);
				FileOutputStream out = new FileOutputStream(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(out)) {
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
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public static long caclFileLineNumber(File file) throws FileNotFoundException, IOException {
		try (FileReader in = new FileReader(file);) {
			LineNumberReader reader = new LineNumberReader(in);
			reader.skip(Long.MAX_VALUE);
			int lines = reader.getLineNumber();
			return lines + 1;
		}
	}

	/**
	 * 文件随机写入内容
	 * 
	 * @param file     文件
	 * @param position 写入位置
	 * @param bytes    字节
	 * @throws IOException
	 */
	public static void randomWrite(File file, long position, byte[] bytes) throws IOException {
		long length = file.length();
		if (position < 0) {
			position += length;
		}
		File tempFile = File.createTempFile("temp", null);
		tempFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(tempFile);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.seek(position);
		byte[] buffer = new byte[4];
		int num = 0;
		while (-1 != (num = raf.read(buffer))) {
			fos.write(buffer, 0, num);
		}
		raf.seek(position);
		raf.write(bytes);
		FileInputStream fis = new FileInputStream(tempFile);
		while (-1 != (num = fis.read(buffer))) {
			raf.write(buffer, 0, num);
		}
		raf.close();
		fis.close();
		fos.close();

	}

	@SuppressWarnings("deprecation")
	public static int[] findAllIndices(File file, String text) throws Exception {
		int[] indices;
		int index = -1, count = 0, i = 0;
		String givenText = FileUtils.readFileToString(file);
		index = givenText.indexOf(text);
		while (index >= 0) {
			count++;
			index = givenText.indexOf(text, index + 1);
		}
		indices = new int[count];
		index = givenText.indexOf(text);
		while (index >= 0) {
			indices[i] = index;
			index = givenText.indexOf(text, index + 1);
			i++;
		}
		return indices;
	}

	public static void deleteFileBom(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		// 开始写临时文件
		InputStream in = getInputStream(fin);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int buffer = cacBuffer(fin.available());
		byte b[] = new byte[buffer];

		int len = 0;
		while (in.available() > 0) {
			len = in.read(b, 0, buffer);
			// out.write(b, 0, len);
			bos.write(b, 0, len);
		}
		in.close();
		fin.close();
		bos.close();
		// 临时文件写完，开始将临时文件写回本文件。
		FileOutputStream out = new FileOutputStream(file);
		out.write(bos.toByteArray());
		out.close();
	}

	public static InputStream getInputStream(InputStream in) throws IOException {
		PushbackInputStream pbIn = new PushbackInputStream(in);
		int ch = pbIn.read();
		if (ch != 0xEF) {
			pbIn.unread(ch);
		} else if ((ch = pbIn.read()) != 0xBB) {
			pbIn.unread(ch);
			pbIn.unread(0xef);
		} else if ((ch = pbIn.read()) != 0xBF) {
			throw new IOException("Error UTF-8 File Format!");
		}
		return pbIn;
	}
}
