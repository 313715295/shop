package com.zwq.shop.apigetway.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * 网上搬来的，序列化数组要比源码大一点点，以user为例，源码7631，这个7676.jkson24500，jdk27000.解决 fastjson autotype is not support
 * fastjson源码也有提供，但是源码序列化是JSON.toJsonBytes。没有"@type"，所以反序列化的时候强转对象有问题
 * @param <T>
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    static {
        //解决 fastjson autotype is not support
        ParserConfig.getGlobalInstance().addAccept("com.zwq");
    }

    public FastJsonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;

    }



    @Override
    public byte[] serialize(T t) throws SerializationException {

        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }
}

