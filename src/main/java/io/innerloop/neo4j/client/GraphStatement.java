package io.innerloop.neo4j.client;

/**
 * This is a statement that returns graphs.
 *
 * @see io.innerloop.neo4j.client.Graph
 * @see io.innerloop.neo4j.client.Statement
 */
public class GraphStatement extends Statement<Graph>
{
    public GraphStatement(String query)
    {
        super(query);
    }

    @Override
    public String getType()
    {
        return "graph";
    }
}
