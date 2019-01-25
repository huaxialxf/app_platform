package com.marksmile.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @Title:Json工具类
 * @Description:
 * @Author : xiaoshipeng
 * @Since : 2015年10月13日
 * @Version : 1.0.0
 */
public class JSONUtil {

    /**
     * object 转 Json
     * 
     * @param object
     * @return
     * @Description :
     */
    public static String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    public static String toJsonStringNoSort(Object object) {
        SerializeWriter out = new SerializeWriter();
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(SerializerFeature.SortField, false);
            serializer.write(object);
            return out.toString();
        } finally {
            out.close();
        }

    }

    /**
     * json 转 List<String>
     * 
     * @param jsonString
     * @return
     * @Description :
     */
    public List<String> parseListString(String jsonString) {
        return JSON.parseArray(jsonString, String.class);
    }

    /**
     * json 转 List<Object>
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @Description :
     */
    public static <T> List<T> parseListObject(String jsonString,
            Class<T> clazz) {
        return JSON.parseArray(jsonString, clazz);
    }

    /**
     * json 转 List<Object>
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @Description :
     */
    public static <T> List<T> parseListJavaBean(String jsonString,
            Class<T> clazz) {
        return JSON.parseArray(jsonString, clazz);
    }

    /**
     * json 转 javaBean
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @Description :
     */
    public static <T> T parseJavaBean(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);

    }

    /**
     * json 转 List<Map<String, String>>
     * 
     * @param jsonString
     * @return
     * @Description :
     */
    public static final List<Map<String, String>> parseObject(
            String jsonString) {
        List<Map<String, String>> listMap = JSON.parseObject(jsonString,
                new TypeReference<List<Map<String, String>>>() {
                });
        return listMap;

    }

    public static void main(String[] args) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("2", "2");
        map.put("1", "1");
        System.out.println(toJsonStringNoSort(map));

    }
}
