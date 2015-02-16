package io.innerloop.neo4j.client;

/**
 * Created by markangrish on 16/02/2015.
 */
public class Neo4jServerException extends RuntimeException
{
    private final String code;

    /**
     * Creates a new Neo4jClientException.
     *
     * @param code
     *         The code provided by the Neo4J Server.
     * @param message
     *         The message provided by the Neo4J Server.
     */
    public Neo4jServerException(String code, String message)
    {
        super(message);
        this.code = code;
    }


    /**
     * Retrieves the short code, usually a java error type from the Neo4J Server. Errors can be looked up by code in the
     * Neo4J documentation.
     *
     * @return a short String with the error type.
     */
    public String getCode()
    {
        return this.code;
    }
}
