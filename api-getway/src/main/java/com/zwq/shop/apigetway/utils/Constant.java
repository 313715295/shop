package com.zwq.shop.apigetway.utils;

/**
 * created by zwq on 2018/10/22
 */
public class Constant {
    /**
     * 请求记录时间，7天，间隔7天没请求的删除
     */
    public static final int REQUEST_RECORD_LIVE_TIME = 7 * 24;
    /**
     * 限定时间内，请求次数限制
     */
    public static final int REQUEST_NUMBER_LIMIT = 20;
    /**
     * 请求限定时间(毫秒)
     */
    public static final int REQUEST_LIMIT_TIME = 20*1000;
}
