package io.innerloop.neo4j.client;

import io.innerloop.neo4j.client.json.JSONArray;
import io.innerloop.neo4j.client.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Neo4J supports two forms fo Statement Requests: Graphs and Rows.
 * <p>
 * Unlike JDBC in which statements are created from a connection, the Neo4J Driver actually allows developers to create
 * as many statements as they like on a single transaction (supported through the Transaction#add(Statement) method).
 * <p>
 * Results are also specific to the statement and can be retrieved via the #getResult() method.
 * <p>
 * NOTE: This API will probably change to support a builder style Statement builder.
 */
public abstract class Statement<T>
{
    protected final String statement;

    protected final Map<String, Object> parameters;

    private final String[] resultDataContents;

    protected boolean includeStats;

    private T result;

    /**
     * Create a new Statement for the provided cypher query.
     *
     * @param query
     *         The cypher query to execute.
     */
    public Statement(String query)
    {
        this.statement = query;
        this.parameters = new HashMap<>();
        this.includeStats = false;
        this.resultDataContents = new String[] {getType()};
    }

    /**
     * Neo4J supports parameter binding in cypher by inserting { paramName } into the query.
     * <p>
     * You can set a parameter using this method.
     *
     * @param key
     *         The parameter to set.
     * @param value
     *         The value to replace the parameter with.
     */
    public void setParam(String key, Object value)
    {
        parameters.put(key, value);
    }

    /**
     * Retrieves the result of this Statement. Only accessible after Transaction#commit() has been called.
     *
     * @return The result of the execution of this statement if available. If called before Transaction#commit(), null
     * is returned.
     */
    public T getResult()
    {
        return result;
    }

    /**
     * Sets the result on this Statement.
     *
     * @param result
     *         The result to set.
     */
    public void setResult(T result)
    {
        this.result = result;
    }

    /**
     * Returns either "row" or "graph".
     *
     * @return The type of this Statement.
     */
    public abstract String getType();

    /**
     * Converts this object to  a JSONObject.
     *
     * @return A JSONObject representing this object.
     */
    public JSONObject toJson()
    {
        return new JSONObject().put("statement", statement)
                       .put("resultDataContents", new JSONArray(resultDataContents))
                       .put("includeStats", includeStats)
                       .put("parameters", new JSONObject(parameters));
    }
}
