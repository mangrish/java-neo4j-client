package io.innerloop.neo4j.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

/**
 * Created by markangrish on 11/12/2014.
 */
public class EndToEndTests
{
    private Neo4jClient client;

    private CommunityNeoServer server;

    @Before
    public void setUp()  throws IOException, InterruptedException, Neo4jClientException
    {
        int port = new ServerSocket(0).getLocalPort();

        server = CommunityServerBuilder.server().onPort(port).build();
        server.start();

        while (!server.getDatabase().isRunning())
        {
            // It's ok to spin here.. it's not production code.
            Thread.sleep(250);
        }
        client = new Neo4jClient("http://localhost:" + port + "/db/data");
    }

    @After
    public void tearDown()
    {
        server.stop();
        client = null;
    }

    @Test
    public void testSimpleQuery() throws Neo4jClientException
    {
        Transaction transaction = client.getAtomicTransaction();

        Statement<RowSet> statement = new RowStatement("MATCH (n) RETURN count(n)");
        transaction.add(statement);
        transaction.commit();

        RowSet result = statement.getResult();
        assertNotNull(result);
        int totalNodes = result.getInt(0);
        assertTrue(totalNodes >= 0);

    }

    @Test
    public void testInsertCompoundStatements() throws Neo4jClientException
    {
        Transaction transaction1 = client.getAtomicTransaction();

        Statement<RowSet> statement1 = new RowStatement("MERGE (n:Label{id:\"id1\", prop1:\"property1\"})");
        Statement<RowSet> statement2 = new RowStatement("MERGE (n:Label{id:\"id2\", prop1:\"property2\"})");
        transaction1.add(statement1);
        transaction1.add(statement2);
        transaction1.commit();

        //check if the nodes were inserted
        Transaction transaction2 = client.getAtomicTransaction();

        Statement<RowSet> statement = new RowStatement("MATCH (n:Label) RETURN count(n) as number_of_nodes");
        transaction2.add(statement);
        transaction2.commit();

        RowSet result = statement.getResult();
        assertNotNull(result);
        int numberOfLabelNodes = result.getInt(0);
        assertEquals(2, numberOfLabelNodes);

    }

    @Test
    public void testGraphCreationAndRetrieval() throws Neo4jClientException
    {
        Transaction transaction1 = client.getAtomicTransaction();

        Statement<RowSet> statement1 = new RowStatement("MERGE (n1:Graph{id:\"id1\", prop1:\"property1\"})-[:connectedTo]-(n2:Graph{id:\"id2\", prop1:\"property2\"})");
        transaction1.add(statement1);
        transaction1.commit();

        //check if the nodes were inserted
        Transaction transaction2 = client.getAtomicTransaction();

        Statement<Graph> statement = new GraphStatement("MATCH (n:Graph)-[rels]-() RETURN rels");
        transaction2.add(statement);
        transaction2.commit();

        Graph result = statement.getResult();
        assertNotNull(result);

        Set<Node> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        Set<Relationship> relationships = result.getRelationships();
        assertEquals(1, relationships.size());
    }

    @Test
    public void testBasicLongTransaction() throws Neo4jClientException
    {
        Transaction transaction1 = client.getLongTransaction();

        try
        {
            transaction1.begin();

            Statement<RowSet> statement1 = new RowStatement("MERGE (n1:Graph{id:\"id1\", prop1:\"property1\"})-[:connectedTo]-(n2:Graph{id:\"id2\", prop1:\"property2\"})");
            Statement<RowSet> statement2 = new RowStatement("MERGE (n2:Graph{id:\"id3\", prop1:\"property3\"})");

            transaction1.add(statement1);
            transaction1.add(statement2);
            transaction1.flush();

            Statement<RowSet> statement3 = new RowStatement("MERGE (n2:Graph{id:\"id4\"}) SET n2 = {props}");
            Map<String, Object> props = new HashMap<>();
            props.put("id", "id4");
            props.put("prop1", "property4");
            props.put("random", 213);
            statement3.setParam("props", props);

            transaction1.add(statement3);
            transaction1.commit();
        }
        catch (Neo4jClientException e)
        {
            transaction1.rollback();
            fail("Should have been able to commit. Rolled back to keep DB consistent.");
        }

        //check if the nodes were inserted
        Transaction transaction2 = client.getAtomicTransaction();

        Statement<Graph> statement = new GraphStatement("MATCH (n:Graph) OPTIONAL MATCH (n)-[rels]-() RETURN n, rels");
        transaction2.add(statement);
        transaction2.commit();

        Graph result = statement.getResult();
        assertNotNull(result);

        Set<Node> nodes = result.getNodes();
        assertEquals(4, nodes.size());

        Set<Relationship> relationships = result.getRelationships();
        assertEquals(1, relationships.size());


    }

    @Test
    public void testMultipleThreadsInsertingCompoundStatements() throws InterruptedException, Neo4jClientException
    {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 1; i <= 100; i++)
        {
            service.execute(new InsertJob(latch, i));
        }

        latch.await();

        //check if the nodes were inserted
        Transaction transaction2 = client.getAtomicTransaction();

        Statement<RowSet> statement = new RowStatement("MATCH (n:BulkInsert) RETURN count(n) as number_of_nodes");
        transaction2.add(statement);
        transaction2.commit();

        RowSet result = statement.getResult();
        assertNotNull(result);
        int numberOfLabelNodes = result.getInt(0);
        assertEquals(1000, numberOfLabelNodes);

    }

    private class InsertJob implements Runnable
    {
        private final CountDownLatch latch;

        private final int id;

        public InsertJob(CountDownLatch latch, int i)
        {
            this.latch = latch;
            this.id = i;
        }

        @Override
        public void run()
        {
            Transaction transaction = client.getAtomicTransaction();

            try
            {
                for (int i = 1; i <= 10; i++)
                {
                    Statement<RowSet> statement1 = new RowStatement("MERGE (n:BulkInsert{id:\"id" + id + i +
                                                                    "\"})");
                    transaction.add(statement1);
                }
                transaction.commit();
            }
            catch (Neo4jClientException e)
            {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < 10; i++)
            {
                latch.countDown();
            }
        }
    }
}
