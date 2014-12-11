package io.innerloop.neo4j.client;

/**
 * A Transaction represents an interaction with the database. All Neo4J Cypher Queries must run within a Transaction,
 * even reads. Thus all reads are READ COMMITTED.
 * <p>
 * Transactions can support the execution of multiple statements. Statements are executed in order of insertion.
 */
public interface Transaction
{
    /**
     * Starts a Transaction.
     */
    void begin();

    /**
     * Adds the given statement to execute within this Transaction. This method can be called multiple times with
     * different statements before calling commit() or rollback().
     *
     * @param statement
     *         A statement that should be added to this Transaction.
     */
    void add(Statement statement);

    /**
     * Commits this Transaction to Neo4J. Right now commit() won't throw a checked exeption. This may change in future.
     */
    void commit();

    /**
     * Rolls back any changes made by this Transaction.
     */
    void rollback();
}
