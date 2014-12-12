package io.innerloop.neo4j.client.spi;

import io.innerloop.neo4j.client.Transaction;

/**
 * Providers of implementations to Neo4J should implement this as an abstract Factory.
 */
public interface TransactionFactory
{
    /**
     * Always creates a new Atomic Transaction. This Transaction can be thought of as "use once, then burn". Either all
     * statements in the Transaction work or it rolls back the Transaction automatically.
     *
     * @return A Transaction that can only be used once.
     */
    Transaction getAtomicTransaction();

    /**
     * Creates a new Long Transaction or retrieves an existing one on this thread. This Transaction is more like a
     * traditional Relational Transaction and is expected to be used over a longer period of time. Nearly every scenario
     * of this Transaction type can be realised with an Atomic Transaction.
     *
     * @return A Transaction that uses the typical begin(), add(Statement), flush()/commit() then rollback() type
     * behaviour.
     */
    Transaction getLongTransaction();
}
