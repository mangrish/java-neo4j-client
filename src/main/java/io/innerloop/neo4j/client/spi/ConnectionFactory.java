package io.innerloop.neo4j.client.spi;

import io.innerloop.neo4j.client.Connection;

/**
 * Providers of implementations to Neo4J should implement this as an abstract Factory.
 */
public interface ConnectionFactory
{
    /**
     * Creates a new Connection or retrieves an existing one on this thread.
     *
     * @return A Connection that uses the typical begin(), add(Statement), flush()/commit() then rollback() type
     * behaviour.
     */
    Connection getConnection();
}
