package io.innerloop.neo4j.client;

/**
 * Created by markangrish on 11/12/2014.
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
