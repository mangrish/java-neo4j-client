package io.innerloop.neo4j.client;

/**
 * An Exception with the error code and message from the Neo4J Server.
 * <p>
 * <p> This is a RuntimeException as there is no reasonable corrective behaviour a program is expected to make when this
 * error occurs. </p>
 */
public class Neo4jClientException extends Exception
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
    public Neo4jClientException(String code, String message)
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
