package io.innerloop.neo4j.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.slf4j.LoggerFactory;

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

    @BeforeClass
    public static void oneTimeSetUp()
    {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("io.innerloop.neo4j.client");
        rootLogger.setLevel(Level.DEBUG);
    }

    @Before
    public void setUp() throws IOException, InterruptedException
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
    public void testSimpleQuery()
    {
        Connection connection = client.getConnection();

        RowStatement statement = new RowStatement("MATCH (n) RETURN count(n)");
        connection.add(statement);
        connection.commit();

        RowSet result = statement.getResult();
        assertNotNull(result);
        int totalNodes = result.getInt(0);
        assertTrue(totalNodes >= 0);

    }

    @Test
    public void testInsertCompoundStatements()
    {
        Connection connection1 = client.getConnection();

        RowStatement statement1 = new RowStatement("MERGE (n:Label{id:\"id1\", prop1:\"property1\"})");
        RowStatement statement2 = new RowStatement("MERGE (n:Label{id:\"id2\", prop1:\"property2\"})");
        connection1.add(statement1);
        connection1.add(statement2);
        connection1.commit();

        //check if the nodes were inserted
        Connection connection2 = client.getConnection();

        RowStatement statement = new RowStatement("MATCH (n:Label) RETURN count(n) as number_of_nodes");
        connection2.add(statement);
        connection2.commit();

        RowSet result = statement.getResult();
        assertNotNull(result);
        int numberOfLabelNodes = result.getInt(0);
        assertEquals(2, numberOfLabelNodes);

    }

    @Test
    public void testGraphCreationAndRetrieval()
    {
        Connection connection1 = client.getConnection();

        RowStatement statement1 = new RowStatement("MERGE (n1:Graph{id:\"id1\", prop1:\"property1\"})-[:connectedTo]-(n2:Graph{id:\"id2\", prop1:\"property2\"})");
        connection1.add(statement1);
        connection1.commit();

        //check if the nodes were inserted
        Connection connection2 = client.getConnection();

        GraphStatement statement = new GraphStatement("MATCH (n:Graph)-[rels]-() RETURN rels");
        connection2.add(statement);
        connection2.commit();

        Graph result = statement.getResult();
        assertNotNull(result);

        Set<Node> nodes = result.getNodes();
        assertEquals(2, nodes.size());

        Set<Relationship> relationships = result.getRelationships();
        assertEquals(1, relationships.size());
    }

    @Test
    public void testBasicLongTransaction()
    {
        Connection connection1 = client.getConnection();

        try
        {
            RowStatement statement1 = new RowStatement("MERGE (n1:Graph{id:\"id1\", prop1:\"property1\"})-[:connectedTo]-(n2:Graph{id:\"id2\", prop1:\"property2\"})");
            RowStatement statement2 = new RowStatement("MERGE (n2:Graph{id:\"id3\", prop1:\"property3\"})");

            connection1.add(statement1);
            connection1.add(statement2);
            connection1.flush();

            RowStatement statement3 = new RowStatement("MERGE (n2:Graph{id:\"id4\"}) SET n2 = {props}");
            Map<String, Object> props = new HashMap<>();
            props.put("id", "id4");
            props.put("prop1", "property4");
            props.put("random", 213);
            statement3.setParam("props", props);

            connection1.add(statement3);
            connection1.commit();
        }
        catch (Neo4jClientException e)
        {
            connection1.rollback();
            fail("Should have been able to commit. Rolled back to keep DB consistent.");
        }

        //check if the nodes were inserted
        Connection connection2 = client.getConnection();

        GraphStatement statement = new GraphStatement("MATCH (n:Graph) OPTIONAL MATCH (n)-[rels]-() RETURN n, rels");
        connection2.add(statement);
        connection2.commit();

        Graph result = statement.getResult();
        assertNotNull(result);

        Set<Node> nodes = result.getNodes();
        assertEquals(4, nodes.size());

        Set<Relationship> relationships = result.getRelationships();
        assertEquals(1, relationships.size());


    }

    @Test
    public void testMultipleThreadsInsertingCompoundStatements() throws InterruptedException
    {
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 1; i <= 100; i++)
        {
            service.execute(new InsertJob(latch, i));
        }

        latch.await();

        //check if the nodes were inserted
        Connection connection2 = client.getConnection();

        RowStatement statement = new RowStatement("MATCH (n:BulkInsert) RETURN count(n) as number_of_nodes");
        connection2.add(statement);
        connection2.commit();

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
            Connection connection = client.getConnection();

            try
            {
                for (int i = 1; i <= 10; i++)
                {
                    RowStatement statement1 = new RowStatement("MERGE (n:BulkInsert{id:\"id" + id + i +
                                                               "\"})");
                    connection.add(statement1);
                }
                connection.commit();
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
