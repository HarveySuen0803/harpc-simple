package com.harvey.common.serializer;

import cn.hutool.json.JSONUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author Harvey Suen
 */
public class SerializerJsonImpl implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String json = gson.toJson(obj);
        return json.getBytes(StandardCharsets.UTF_8);
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String json = new String(bytes, StandardCharsets.UTF_8);
        return gson.fromJson(json, cls);
    }
    
    /**
     * 让 Gson 支持序列化 Class
     */
    public static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }
        
        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }
    }
}
