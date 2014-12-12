package io.innerloop.neo4j.client;

import io.innerloop.neo4j.client.spi.TransactionFactory;
import io.innerloop.neo4j.client.spi.impl.resty.RestyTransactionFactoryImpl;


/**
 * This is the main class to use! <p> <p>This will create a new client to use for an application. You should  only need
 * one instance of this class to service a whole application. Once instantiated this class can be considered thread
 * safe.</p>
 */
public class Neo4jClient
{
    private TransactionFactory transactionFactory;

    /**
     * Create a new Client.
     *
     * @param url
     *         The URL to connect this client to. This constructor is generally used for DMZ installations and testing
     *         purposes.
     */
    public Neo4jClient(String url)
    {
        this.transactionFactory = new RestyTransactionFactoryImpl(url);
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
        this.transactionFactory = new RestyTransactionFactoryImpl(url, userName, password);
    }

    /**
     * Retrieve a new Atomic Connection. Most Neo4J usage scenarios can be implemented with an Atomic Transaction. That
     * is this transaction supports multiple statements being added to it and executed in an all or nothing effect that
     * works within the ACID conditions of Neo4J. <p> <p>Users will not have to rollback transactions. An Atomic
     * Transaction is a once off execution that will commit all statements in the transaction or none at all</p>
     *
     * @return An Atomic Transaction. begin() and rollback() have no use.
     */
    public Transaction getAtomicTransaction()
    {
        return transactionFactory.getAtomicTransaction();
    }

    /**
     * Create a new Long Transaction or retrieve the existing one on the currently executing Thread.
     * Unlike Atomic Transactions, Long Transactions make use of begin() and rollback() but come with a performance
     * penalty when compared to Atomic Transactions.
     *
     * @return A Long Transaction.
     */
    public Transaction getLongTransaction()
    {
        return transactionFactory.getLongTransaction();
    }
}
