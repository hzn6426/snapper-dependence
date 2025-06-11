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
