package com.baomibing.tool.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

/**
 * Cp56Time2aUtil
 *
 * @author zening 2024/5/7 下午9:05
 * @version 1.0.0
 **/
public class Cp56Time2aUtil {

    /**
     * Cp56Time2a转时间字符串
     * @param bytes 字符数组
     */
    public static String toDateString(byte[] bytes) {
        int milliseconds1 = bytes[0] < 0 ? 256 + bytes[0] : bytes[0];
        int milliseconds2 = bytes[1] < 0 ? 256 + bytes[1] : bytes[1];
        int milliseconds = milliseconds2 * 256 + milliseconds1 ;
        // 位于 0011 1111
        int minutes = bytes[2] & 0x3F;
        // 位于 0001 1111
        int hours = bytes[3] & 0x1F;
        // 位于 0001 1111
        int days = bytes[4] & 0x1F;
        // 位于 0000 1111
        int months = bytes[5] & 0x0F;
        // 位于 0111 1111
        int years = bytes[6] & 0x7F;
        return "20" + String.format("%02d", years) + "-" + String.format("%02d", months) + "-" + String.format("%02d", days) +
                " " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" +
                String.format("%02d", milliseconds / 1000);
    }

    /**
     * 转为时间格式
     * @param bytes 字符数组
     */
    public static Date toDate(byte[] bytes) {
        String dateString = toDateString(bytes);
        return DateUtil.parse(dateString,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 时间转16进制字符串
     * @param date 时间
     */
    public static String date2HexStr(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        StringBuilder builder = new StringBuilder();
        String milliSecond = String.format("%04X", (calendar.get(Calendar.SECOND) * 1000) + calendar.get(Calendar.MILLISECOND));
        builder.append(milliSecond.substring(2, 4));
        builder.append(milliSecond.substring(0, 2));
        builder.append(String.format("%02X", calendar.get(Calendar.MINUTE) & 0x3F));
        builder.append(String.format("%02X", calendar.get(Calendar.HOUR_OF_DAY) & 0x1F));
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY){
            week = 7;
        } else {
            week--;
        }
        builder.append(String.format("%02X", (week << 5) + (calendar.get(Calendar.DAY_OF_MONTH) & 0x1F)));
        builder.append(String.format("%02X", calendar.get(Calendar.MONTH) + 1));
        builder.append(String.format("%02X", calendar.get(Calendar.YEAR) - 2000));
        return builder.toString();
    }

    /**
     * BCD码转为10进制串(阿拉伯数据)
     * @param bytes
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * 10进制串转为BCD码
     * @param asc
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }


    public static byte[] get4bPrice(BigDecimal price) {

        int price_int = price.movePointRight(5).intValue();//右移5位，与文档要求一致

        ByteBuffer byteBuffer = ByteBuffer.allocate(4); //这里的4是与设备下发要求的长度一致
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN); //这里是小端模式
        byteBuffer.putInt(price_int);
        byte[] bytes =byteBuffer.array();
        return bytes;
    }


    public static void main(String[] args) {
//        String decodeStr = "A7232E0FB80217";
//        String originStr = "2023-02-24 15:46:09";
//        byte[] byteTimes = HexUtil.decodeHex(decodeStr);
//        System.out.println("Cp56Time2a原值: " + originStr);
//        System.out.println("解析"+ decodeStr + ": " + toDateString(byteTimes));
//        Date time = toDate(byteTimes);
//        String hexString = date2HexStr(new Date());
//        System.out.println("新Cp56Time2a: " + hexString);
//        System.out.println("新Cp56Time2a原值: " + toDateString(HexUtil.decodeHex(hexString)));

        String date = "2020-03-16 17:14:47";
        String hexString = date2HexStr(DateUtil.parse(date, "yyyy-MM-dd HH:mm:ss"));
        System.out.println("新Cp56Time2a: " + hexString);
        System.out.println("新Cp56Time2a原值: " + toDateString(HexUtil.decodeHex(hexString)));

    }
}
