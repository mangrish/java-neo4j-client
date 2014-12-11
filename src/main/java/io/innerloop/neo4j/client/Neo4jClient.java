package io.innerloop.neo4j.client;

import io.innerloop.neo4j.client.spi.TransactionFactory;
import io.innerloop.neo4j.client.spi.impl.resty.RestyTransactionFactoryImpl;


/**
 * Created by markangrish on 24/10/2014.
 * <p>
 * TODO: In future a simple classpath scan to find an implementing class for the connection factory will be used.
 */
public class Neo4jClient
{
    public TransactionFactory transactionFactory;

    public Neo4jClient(String url)
    {
        this.transactionFactory = new RestyTransactionFactoryImpl(url);
    }

    public Neo4jClient(String url, String userName, String password)
    {
        this.transactionFactory = new RestyTransactionFactoryImpl(url, userName, password);
    }

    public Transaction getAtomicTransaction()
    {
        return transactionFactory.createAtomicTransaction();
    }
}
