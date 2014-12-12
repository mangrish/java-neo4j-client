package io.innerloop.neo4j.client;

/**
 * A Transaction represents an interaction with the database. All Neo4J Cypher Queries must run within a Transaction,
 * even reads. Thus all reads are always READ COMMITTED.
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
     * different statements before calling flush(), commit() or rollback().
     *
     * @param statement
     *         A statement that should be added to this Transaction.
     */
    void add(Statement statement);

    /**
     * Flushes the Statements currently held by this transaction to Neo4J. Once this method is called any statements
     * issued before a call to this method will have their results available. Note that any reads in this Transaction
     * will still only be READ COMMITTED. Writes in this Transaction will be UNCOMMITTED until commit() is called. That
     * is this transaction will be fully isolated until commit() is called. Calling Rollback will not roll back
     * statements to this point (like a savepoint) rather it will rollback the entire transaction.
     */
    void flush();

    /**
     * Commits this Transaction to Neo4J. Right now commit() won't throw a checked exeption. This may change in future.
     */
    void commit();

    /**
     * Rolls back any changes made by this Transaction.
     */
    void rollback();

    /**
     * Closes this Transaction. Allows any cleanup operation by the implementer to take place.
     */
    void close();
}
