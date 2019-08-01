package com.andychan.gateway.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 16/11/25.
 */
public class ObjectJsonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectJsonUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> T getObjectFromJsonString(TypeReference<T> t, String json)   {
        if(json==null){
            return null;
        }
        T object = null;
        try {
            object = mapper.readValue(json, t);
        } catch (Exception e) {
            LOG.error("get object from json string fail.", e);
            LOG.error("The input json is {}.", json);
        }
        return object;
    }


    public static <T> T getObjectFromJsonString(Class<T> t,  String json)   {
        T object = null;
        try {
            object = mapper.readValue(json, t);
        } catch (Exception e) {
            LOG.error("get object from json string fail.", e);
            LOG.error("The input json is {}.", json);
        }
        return object;
    }

    public static <T> String getJsonStringFromObject(T t)  {

        if(t ==null) {
            return null;
        }
        String json  = "";
        try {
            json = mapper.writeValueAsString(t);
        } catch (Exception e) {
            LOG.error("get json string from object fail.", e);
            LOG.error("the original json String is {}.", t);
        }
        return json;
    }

}
