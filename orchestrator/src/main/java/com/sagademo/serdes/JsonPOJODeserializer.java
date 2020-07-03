package com.sagademo.serdes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Deserializer;

public class JsonPOJODeserializer<T> implements Deserializer<T> {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Class<T> tClass;

    public JsonPOJODeserializer(Class<T> clazz){
        this.tClass = clazz;
    }

    @Override
    public T deserialize(final String topic, final byte[] bytes) {
        String s = new String(bytes);
        if (bytes == null) {
            return null;
        }

        T data;
        try {
            data = objectMapper.readValue(bytes, tClass);
        } catch (final Exception e) {
            logger.warning("Failed to convert " + s + " to " + tClass.getName());
            return null;
        }

        return data;
    }

    @Override
    public void close() {

    }

    private Type findMyType(Class<?> plainClass, Type genericClass) {
        Class<?> plainSuper = plainClass.getSuperclass();
        Type genericSuper = plainClass.getGenericSuperclass();

        Type t;

        if (plainSuper == JsonPOJODeserializer.class) {
            t = ((ParameterizedType) genericSuper).getActualTypeArguments()[0];
        } else {
            t = findMyType(plainSuper, genericSuper);
        }

        if (t instanceof TypeVariable<?>) {
            TypeVariable<?>[] vars = plainClass.getTypeParameters();

            for (int i = 0; i < vars.length; ++i) {
                if (t == vars[i]) {
                    t = ((ParameterizedType) genericClass).getActualTypeArguments()[i];
                    break;
                }
            }
        }

        return t;
    }
}