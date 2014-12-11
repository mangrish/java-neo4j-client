package io.innerloop.neo4j.client;

/**
 * The Neo4J Protocol does not make it clear if multiple errors can occur on an atomic transaction. <p> <p> Until
 * further notice this Exception will collect all errors just in case.</p>
 */
public class Neo4jClientMultiException extends RuntimeException
{
    private final Neo4jClientException[] exceptions;

    /**
     * Creates a new Neo4jClientMultiException.
     *
     * @param message
     *         A general message provided by the client driver.
     * @param exceptions
     *         An array of Neo4jClientException's.
     */
    public Neo4jClientMultiException(String message, Neo4jClientException[] exceptions)
    {
        super(message);
        this.exceptions = exceptions;
    }

    @Override
    public String toString()
    {
        String message = super.toString();

        for (Neo4jClientException exception : exceptions)
        {
            message += "\nException: [\n" + exception.toString() + "]\n";
        }

        return message;
    }
}
