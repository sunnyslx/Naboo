package net.imoran.tv.sdk.network.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * Created by bobge on 2017/8/16.
 * GSON的简单使用,目前只处理0，1与ture,false之间的适配
 * 时间格式的统一输出可以配置，暂时不配
 */
public final class GsonObjectDeserializer {

    public static LinkedHashMap deserialize(JsonElement jsonElement) {
        Gson gson = produceGson();
        return gson.fromJson(jsonElement, LinkedHashMap.class);
    }

    public static <T> T deserialize(JsonElement jsonElement, Class<T> type) {
        Gson gson = produceGson();
        return gson.fromJson(jsonElement, type);
    }

    public static <T> T deserialize(String jsonString, Class<T> type) {
        Gson gson = produceGson();
        return gson.fromJson(jsonString, type);
    }

    private static final String DefaultDateFormatPattern = "yyyyMMddHHmmss";

    public static Gson produceGson() {

        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return clazz == Field.class || clazz == Method.class;
            }
        };
        //启用enableComplexMapKeySerialization配置项，并且启用类型适配器
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                //   .setDateFormat(DefaultDateFormatPattern)
                .enableComplexMapKeySerialization()
                .create();
        return gson;
    }

}
