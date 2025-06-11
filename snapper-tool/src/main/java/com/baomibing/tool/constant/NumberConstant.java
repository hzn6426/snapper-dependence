/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.tool.constant;

/**
 * NumberConstant
 *
 * @author zening 2023/5/11 22:48
 * @version 1.0.0
 **/
public abstract class NumberConstant {

    //=======================================================================//
    //                            About Time Constant                          //
    //=======================================================================//
    public final static long HALF_AN_HOUR_SECONDS = 30 * 60 ;

    public final static long ONE_HOUR_SECONDS = 60 * 60 ;

    public final static long ONE_DAY_SECONDS = 86400;

    //=======================================================================//
    //                      System Common Constants                          //
    //=======================================================================//
    public static final long MAX_UPLOAD_FILE_SIZE = 2 * 1024 * 1024;
    public static final long MAX_DOWNLOAD_FILE_SIZE = 10 * 1024 * 1024;

    //in的最大查询数量
    public static final int MAX_IN_BATCH_SIZE = 200;

    //=======================================================================//
    //                            About IO Constant                          //
    //=======================================================================//
    /**
     * 最大一次性发送邮件数量
     */
    public static final int MAX_ONCE_SEND_MAIL_SIZE = 20;
    /**
     * 最大一次性接收邮件的数量
     */
    public static final int MAX_ONCE_RECEIVE_MAIL_SIZE = 10;
    /**
     * 一封邮件附件的最大数量
     */
    public static final int MAX_FILE_NUMBER_IN_ONE_MAIL = 5;
    /**
     * 邮件发送附件的最大值
     */
    public static final long MAX_MAIL_FILE_SIZE = 10 * 1024 * 1024;
}
