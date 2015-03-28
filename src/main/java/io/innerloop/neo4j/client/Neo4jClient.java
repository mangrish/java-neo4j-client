package io.innerloop.neo4j.client;

import io.innerloop.neo4j.client.spi.ConnectionFactory;
import io.innerloop.neo4j.client.spi.impl.rest.RestConnectionFactoryImpl;


/**
 * This is the main class to use! <p> <p>This will create a new client to use for an application. You should  only need
 * one instance of this class to service a whole application. Once instantiated this class can be considered thread
 * safe.</p>
 */
public class Neo4jClient
{
    private ConnectionFactory connectionFactory;

    /**
     * Create a new Client.
     *
     * @param url
     *         The URL to connect this client to. This constructor is generally used for DMZ installations and testing
     *         purposes.
     */
    public Neo4jClient(String url)
    {
        this.connectionFactory = new RestConnectionFactoryImpl(url);
    }

    /**
     * Create a new Client for a protected Neo4J Instance.
     *
     * @param url
     *         THe URL to connect this client to.
     * @param userName
     *         The username for authentication purposes to this Neo4J Installation.
     * @param password
     *         The password for authentication purposes to this Neo4J Installation.
     */
    public Neo4jClient(String url, String userName, String password)
    {
        this.connectionFactory = new RestConnectionFactoryImpl(url, userName, password);
    }

    /**
     * Create a new Connection or retrieve the existing one on the currently executing Thread.
     *
     * @return A Connection.
     */
    public Connection getConnection()
    {
        return connectionFactory.getConnection();
    }
}
