package com.harvey.common.serializer;

/**
 * @author Harvey Suen
 */
public class SerializerFactory {
    
    
    public static Serializer getSerializer(int serializerType) {
        if (serializerType == 1) {
            return new SerializerJavaImpl();
        } else if (serializerType == 2) {
            return new SerializerJsonImpl();
        } else {
            return new SerializerJavaImpl();
        }
    }
}
