//package com.zwq.shop.apigetway.serializer;
//
//import io.protostuff.LinkedBuffer;
//import io.protostuff.ProtostuffIOUtil;
//import io.protostuff.Schema;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//
//import java.nio.charset.Charset;
//
//import static io.protostuff.runtime.RuntimeSchema.getSchema;
//
//
///**
// * 反序列化需要用到Class类型，暂时没找到办法~先弃用
// *
// * @param <T>
// */
//@Slf4j
//public class ProtostuffSerializer<T> implements RedisSerializer<T> {
//
//    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
//
//    private Class<T> clazz;
//
//    public ProtostuffSerializer(Class<T> clazz) {
//        this.clazz = clazz;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public byte[] serialize(T t) throws SerializationException {
//        Class<T> cls = (Class<T>) t.getClass();
//        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
//        try {
//            Schema<T> schema = getSchema(cls);
//            return ProtostuffIOUtil.toByteArray(t, schema, buffer);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        } finally {
//            buffer.clear();
//        }
//    }
//
//    @Override
//    public T deserialize(byte[] paramArrayOfByte) throws SerializationException {
//        T instance;
//        if (paramArrayOfByte.length == 0) {
//            log.error("Failed to deserialize, byte is empty");
//            throw new RuntimeException("Failed to deserialize");
//        }
//        try {
//            Schema<T> schema = getSchema(clazz);
//            instance = schema.newMessage();
//            ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        }
//        return instance ;
//    }
//
//
//}
