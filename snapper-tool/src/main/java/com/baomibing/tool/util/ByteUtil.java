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

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * ByteUtil
 *
 * @author zening 2024/5/7 下午8:57
 * @version 1.0.0
 **/
public class ByteUtil {

    /**
     * 计费规则用  4byte  5位小数
     *
     * @param price
     */
    public static byte[] get4bPrice(BigDecimal price) {

        int price_int = price.movePointRight(5).intValue();//右移5位，与文档要求一致

        ByteBuffer byteBuffer = ByteBuffer.allocate(4); //这里的4是与设备下发要求的长度一致
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); //这里是小端模式
        byteBuffer.putInt(price_int);
        byte[] bytes =byteBuffer.array();
        return bytes;
    }

    public static void main(String[] args) {
        //D0FB0100
        System.out.println(get4bPrice(new BigDecimal("1.30000")));
    }
}
