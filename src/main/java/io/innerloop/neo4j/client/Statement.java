package io.innerloop.neo4j.client;

import io.innerloop.neo4j.client.json.JSONArray;
import io.innerloop.neo4j.client.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markangrish on 04/11/2014.
 */
public abstract class Statement<T>
{
    protected final String statement;

    protected final Map<String, Object> parameters;

    private final String[] resultDataContents;

    protected boolean includeStats;

    private T result;

    public Statement(String query)
    {
        this.statement = query;
        this.parameters = new HashMap<>();
        this.includeStats = false;
        this.resultDataContents = new String[] {getType()};
    }

    public void setParam(String key, Object value)
    {
        parameters.put(key, value);
    }

    public T getResult()
    {
        return result;
    }

    public void setResult(T result)
    {
        this.result = result;
    }

    public abstract String getType();

    public JSONObject toJson()
    {
        return new JSONObject().put("statement", statement)
                       .put("resultDataContents", new JSONArray(resultDataContents))
                       .put("includeStats", includeStats).put("parameters", new JSONObject(parameters));
    }
}
