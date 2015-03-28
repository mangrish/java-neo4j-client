package io.innerloop.neo4j.client.spi.impl.rest;


import io.innerloop.neo4j.client.spi.impl.rest.json.JSONArray;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONException;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONObject;

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
    static Map<String, Object> jsonToMap(JSONObject json) throws JSONException
    {
        Map<String, Object> retMap = new HashMap<>();

        if (json != JSONObject.NULL)
        {
            retMap = toMap(json);
        }
        return retMap;
    }

    static Map<String, Object> toMap(JSONObject object) throws JSONException
    {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext())
        {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if (value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    static List toList(JSONArray array) throws JSONException
    {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++)
        {
            Object value = array.get(i);
            if (value instanceof JSONArray)
            {
                value = toList((JSONArray) value);
            }

            else if (value instanceof JSONObject)
            {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
