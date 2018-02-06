package cn.com.hellowood.mapper.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;


/**
 * JSON data util, for parse and generate JSON data
 *
 * @author HelloWood
 * @date 2017-07-11 16:11
 */


public class JSONUtil<T> {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * Transfer object to JSON string
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        String result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        //set config of JSON
        // can use single quote
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //allow unquoted field names
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //set date format
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            result = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Generate JSON String error!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    public static Object parseJSONToObj(String jsonString, Class clazz) {
        Object object = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            object = objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.error("Parse JSON String error!" + e.getMessage());
            e.printStackTrace();
        }
        return object;
    }
}
