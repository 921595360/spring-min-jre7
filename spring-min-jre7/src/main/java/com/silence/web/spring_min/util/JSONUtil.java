package com.silence.web.spring_min.util;


import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *   
 * JSONUtil  
 *   
 * silence  
 * silence  
 * 2016年3月20日 下午7:46:37  
 *   
 * @version 1.0.0  
 *
 */
public class JSONUtil {


    public static Map toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);
        
        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;
        
        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);

        }
        return result;

    }

    public static JSONObject toJSON(Object bean) {
        JSONObject o=new JSONObject(bean);
        return o;
    }
    public static JSONArray toArrayJSON(ArrayList bean) {
        JSONArray o=new JSONArray(bean);
        return o;
    }

    public static Object toJavaBean(Object javabean, Map data) {

        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {

            try {
                if (method.getName().startsWith("set")) {

                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, new Object[] {

                    data.get(field)

                    });

                }
            } catch (Exception e) {
            }

        }

        return javabean;

    }

    public static void toJavaBean(Object javabean, String jsonString)
            throws ParseException, JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);
    
        Map map = toMap(jsonObject.toString());
        
        toJavaBean(javabean, map);

    }

}
