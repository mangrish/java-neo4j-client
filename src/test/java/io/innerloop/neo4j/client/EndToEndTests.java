package io.innerloop.neo4j.client;

import org.junit.Test;

/**
 * Created by markangrish on 11/12/2014.
 */
public class EndToEndTests
{

    @Test
    public void testSimpleQuery() {
        Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");
        Transaction transaction = client.getAtomicTransaction();
        Statement<RowSet> statement = new RowStatement("MATCH (n) RETURN count(n)");
        transaction.add(statement);
        transaction.commit();
        RowSet result = statement.getResult();
        int totalNodes = result.getInt(0);
        System.out.print(totalNodes);
    }
}
