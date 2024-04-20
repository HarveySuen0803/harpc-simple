package com.harvey.common.serializer;

/**
 * @author Harvey Suen
 */
public interface Serializer {
    <T> byte[] serialize(T obj);
    
    <T> T deserialize(byte[] bytes, Class<T> cls);
}
