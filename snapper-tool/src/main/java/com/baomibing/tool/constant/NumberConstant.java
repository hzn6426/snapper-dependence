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
