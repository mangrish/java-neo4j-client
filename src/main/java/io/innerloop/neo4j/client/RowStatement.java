package io.innerloop.neo4j.client;

/**
 * Created by markangrish on 11/12/2014.
 */
public class RowStatement extends Statement<RowSet>
{
    public RowStatement(String query)
    {
        super(query);
    }

    @Override
    public String getType()
    {
        return "row";
    }
}
