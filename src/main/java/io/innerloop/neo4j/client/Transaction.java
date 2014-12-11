package io.innerloop.neo4j.client;

/**
 * Created by markangrish on 02/12/2014.
 */
public interface Transaction
{
    void begin();

    void add(Statement statement);

    void commit();

    void rollback();
}
