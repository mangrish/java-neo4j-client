package io.innerloop.neo4j.client;

/**
 * An Exception with the error code and message from the Neo4J Server.
 * <p>
 * <p> This is a RuntimeException as there is no reasonable corrective behaviour a program is expected to make when this
 * error occurs. </p>
 */
public class Neo4jClientRuntimeException extends RuntimeException
{
    public Neo4jClientRuntimeException(String message)
    {
        super(message);
    }

    public Neo4jClientRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public Neo4jClientRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
