package io.innerloop.neo4j.client;

import java.util.List;

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
     * Retrieves any Statements that have not been executed in the cache.
     * <p>
     * <p>This will be empty if flush or commit is called before accessing this.</p>
     *
     * @return A list of statements scheduled to be executed.
     */
    List<Statement> getStatements();

    /**
     * Flushes the Statements currently held by this transaction to Neo4J. Once this method is called any statements
     * issued before a call to this method will have their results available. Note that any reads in this Transaction
     * will still only be READ COMMITTED. Writes in this Transaction will be UNCOMMITTED until commit() is called. That
     * is this transaction will be fully isolated until commit() is called. Calling Rollback will not roll back
     * statements to this point (like a savepoint) rather it will rollback the entire transaction including any flush()s
     * called previously.
     *
     * @throws Neo4jClientRuntimeException
     *         If an error occurs in flushing to the database. It is generally expected that a client will not be able
     *         to recover from this error.
     * @throws Neo4jServerException
     *         If the Neo4J Server responds with an error.
     * @throws Neo4jServerMultiException
     *         If the Neo4J Server responds with with multiple errors (this probably doesn't happen but there is no way
     *         to know for sure).
     */
    void flush();

    /**
     * Commits this Transaction to Neo4J.
     *
     * @throws Neo4jClientException
     *         If an error occurs in committing this transaction to the database. Clients may then choose to catch and
     *         rollback this transaction if desired.
     */
    void commit() throws Neo4jClientException;

    /**
     * Rolls back any changes made by this Transaction.
     *
     * @throws Neo4jClientRuntimeException
     *         If an error occurs trying to rollback the transaction.
     * @throws Neo4jServerException
     *         If the Neo4J Server responds with an error.
     * @throws Neo4jServerMultiException
     *         If the Neo4J Server responds with with multiple errors (this probably doesn't happen but there is no way
     *         to know for sure).
     */
    void rollback();
}
