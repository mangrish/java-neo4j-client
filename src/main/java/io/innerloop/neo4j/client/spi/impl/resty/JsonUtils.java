package io.innerloop.neo4j.client.spi.impl.resty;



import io.innerloop.neo4j.client.json.JSONArray;
import io.innerloop.neo4j.client.json.JSONException;
import io.innerloop.neo4j.client.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by markangrish on 05/11/2014.
 */
class JsonUtils
{
    static Map jsonToMap(JSONObject json) throws JSONException
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    static Map toMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext())
        {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    static List toList(JSONArray array) throws JSONException
    {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static String mapToJson(Map<String, Object> parameters)
    {
        return null;
    }
}
