package me.alvin.learn.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang3.StringUtils;


/**
 * 使用jackson的Json工具类
 *
 * @author: Li Xiang
 * Date: 2021/11/5
 * Time: 5:16 PM
 */
public class JacksonUtils {
    private final static Logger LOGGER = Logger.getInstance(JacksonUtils.class);


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }


    public static <T> T parse(String content, Class<T> valueType) {
        try {
            T result = OBJECT_MAPPER.readValue(content, valueType);
            return result;
        } catch (Exception e) {
            LOGGER.error("Json parse failed ", e);
            return null;
        }
    }

    public static String toJson(Object value) {
        return toJson(value, false);
    }

    public static String toJson(Object value, boolean prettyPrint) {
        try {
            if (prettyPrint) {
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
            } else {
                return OBJECT_MAPPER.writeValueAsString(value);
            }

        } catch (Exception e) {
            LOGGER.error("toJson failed ", e);
            return StringUtils.EMPTY;
        }
    }


    public static <T> T parse(String value, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(value, typeReference);
        } catch (Exception e) {
            LOGGER.error("parse from json failed ", e);
            return null;
        }
    }

    public static <T> T parse(String value, Class<?> container, Class<?>... actualType) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(container, actualType);
            return OBJECT_MAPPER.readValue(value, javaType);
        } catch (Exception e) {
            LOGGER.error("parse from json failed ", e);
            return null;
        }
    }

}
