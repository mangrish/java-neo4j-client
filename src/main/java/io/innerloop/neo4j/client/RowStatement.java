package io.innerloop.neo4j.client;

/**
 * This is a statement that returns RowSets.
 *
 * @see io.innerloop.neo4j.client.RowSet
 * @see io.innerloop.neo4j.client.Statement
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
