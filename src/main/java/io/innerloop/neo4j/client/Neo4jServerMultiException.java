package io.innerloop.neo4j.client;

/**
 * The Neo4J Protocol does not make it clear if multiple errors can occur on an atomic transaction. <p> <p> Until
 * further notice this Exception will collect all errors just in case.</p>
 */
public class Neo4jServerMultiException extends RuntimeException
{
    private final Neo4jServerException[] exceptions;

    /**
     * Creates a new Neo4jClientMultiException.
     *
     * @param message
     *         A general message provided by the client driver.
     * @param exceptions
     *         An array of Neo4jClientException's.
     */
    public Neo4jServerMultiException(String message, Neo4jServerException[] exceptions)
    {
        super(message);
        this.exceptions = exceptions;
    }

    @Override
    public String toString()
    {
        String message = super.toString();

        for (Neo4jServerException exception : exceptions)
        {
            message += "\nException: [\n" + exception.toString() + "]\n";
        }

        return message;
    }
}
