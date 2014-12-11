package io.innerloop.neo4j.client.spi;

import io.innerloop.neo4j.client.Transaction;

/**
 * Created by markangrish on 05/11/2014.
 */
public interface TransactionFactory
{
    Transaction createAtomicTransaction();
}
