/**
 * Copyright (c) 2018-2025, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.authority.util;


import com.baomibing.tool.constant.Strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @Author: zhenyang 2021/4/9 14:58
 * @version: 1.0.0
 */
public class FileUtils {

    /**
     * 精确计算base64字符串文件大小（单位：kB）
     *
     * @param base64String
     * @return
     */
    public static String base64FileSize(String base64String) {
        /**检测是否含有base64,文件头)*/
        if (base64String.lastIndexOf(",") > 0) {
            base64String = base64String.substring(base64String.lastIndexOf(",") + 1);
        }
        /** 获取base64字符串长度(不含data:audio/wav;base64,文件头) */
        int size0 = base64String.length();
        /** 获取字符串的尾巴的最后10个字符，用于判断尾巴是否有等号，正常生成的base64文件'等号'不会超过4个 */
        String tail = base64String.substring(size0 - 10);
        /** 找到等号，把等号也去掉,(等号其实是空的意思,不能算在文件大小里面) */
        int equalIndex = tail.indexOf("=");
        if (equalIndex > 0) {
            size0 = size0 - (10 - equalIndex);
        }
        /** 计算后得到的文件流大小，单位为字节 */
        return (size0 - (size0 / 8) * 2) / 1024 + Strings.EMPTY;
    }


    /**
     * base64转inputStream
     *
     * @param base64string
     * @return
     */
    public static InputStream BaseToInputStream(String base64string) throws IOException {
//      BASE64Decoder decoder = new BASE64Decoder();
//      byte[] bytes = decoder.decodeBuffer(base64string);
    	byte[] bytes = Base64.getDecoder().decode(base64string);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return stream;
    }
}
