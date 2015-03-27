package io.innerloop.neo4j.client;


/**
 * Raise this for local client exceptions rather than exceptions coming back from the Neo4J Server. e.g. disconnections
 * <p> <p> This is a checked Exception as users may want to take action upon receiving this error. </p>
 */
public class Neo4jClientException extends RuntimeException
{

    public Neo4jClientException(String message)
    {
        super(message);
    }

    public Neo4jClientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public Neo4jClientException(Throwable cause)
    {
        super(cause);
    }
}
