package io.innerloop.neo4j.client.spi;

import io.innerloop.neo4j.client.Transaction;

/**
 * Providers of implementations to Neo4J should implement this as an abstract Factory.
 */
public interface TransactionFactory
{
    /**
     * Creates a new Atomic Transaction. This Transaction can be thought of as "use once, then burn". Either all
     * statements in the Transaction work or it rolls back the Transaction automatically.
     *
     * @return A Transaction that can only be used once.
     */
    Transaction createAtomicTransaction();

    /**
     * Creates a new Long Transaction. This Transaction is more like a traditional Relational Transaction and is
     * expected to be used over a longer period of time. Nearly every scenario of this Transaction type can be realised
     * with an Atomic Transaction.
     *
     * @return A Transaction that uses the typical begin(), add(Statement), commit() or rollback() type behaviour.
     */
    Transaction createLongTransaction();
}
