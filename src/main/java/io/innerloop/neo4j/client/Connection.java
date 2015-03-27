package io.innerloop.neo4j.client;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A Connection represents an interaction with the database. All Neo4J Cypher Queries must run within a Connection,
 * even reads. Thus all reads are always READ COMMITTED.
 * <p>
 * Connections can support the execution of multiple statements. Statements are executed in order of insertion.
 */
public interface Connection
{
    /**
     * Adds the given statement to execute within this Connection. This method can be called multiple times with
     * different statements before calling flush(), commit() or rollback().
     *
     * @param statement
     *         A statement that should be added to this Connection.
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
     * Retrieves when this Connection will expire.
     *
     * @return A date time specifying when this Connection will expire..
     */
    LocalDateTime getExpiry();

    /**
     * Extends the life of this Connection. Can be used in conjunction with getExpiry() in a daemon thread to
     * monitor expiry. TODO: NOT CURRENTLY THREADSAFE
     */
    void resetExpiry();

    /**
     * Flushes the Statements currently held by this Connection to Neo4J. Once this method is called any statements
     * issued before a call to this method will have their results available. Note that any reads in this Connection
     * will still only be READ COMMITTED. Writes in this Connection will be UNCOMMITTED until commit() is called. That
     * is this Connection will be fully isolated until commit() is called. Calling Rollback will not roll back
     * statements to this point (like a savepoint) rather it will rollback the entire Connection including any flush()s
     * called previously.
     *
     * @throws Neo4jServerException
     *          If an error occurs in flushing to the database. It is generally expected that a client will not be able
     *         to recover from this error.
     * @throws Neo4jServerMultiException
     *         If the Neo4J Server responds with with multiple errors (this probably doesn't happen but there is no way
     *         to know for sure).
     */
    void flush();

    /**
     * Commits this Connection to Neo4J.
     *
     * @throws Neo4jClientException
     *         If an error occurs in committing this Connection to the database. Clients may then choose to catch and
     *         rollback this Connection if desired.
     */
    void commit();

    /**
     * Rolls back any changes made by this Connection.
     *
     * @throws Neo4jServerException
     *          If an error occurs trying to rollback the Connection.
     * @throws Neo4jServerMultiException
     *         If the Neo4J Server responds with with multiple errors (this probably doesn't happen but there is no way
     *         to know for sure).
     */
    void rollback();
}
