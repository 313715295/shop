package com.zwq.shop.apigetway.enums;

/**
 * created by zwq on 2018/10/22
 */
public enum  RedisKeyEnum {

    REQUEST_RECORD_KEY("shop:request:record:");

    private String key;

     RedisKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
