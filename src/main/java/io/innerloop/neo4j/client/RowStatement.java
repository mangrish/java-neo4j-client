package io.innerloop.neo4j.client;

/**
 * This is a statement that returns RowSets.
 *
 * @see io.innerloop.neo4j.client.RowSet
 * @see io.innerloop.neo4j.client.Statement
 */
public class RowStatement extends Statement
{
    private RowSet result;

    public RowStatement(String query)
    {
        super(query);
    }

    @Override
    public String getType()
    {
        return "row";
    }

    /**
     * Retrieves the result of this Statement. Only accessible after Connection#commit() has been called.
     *
     * @return The result of the execution of this statement if available. If called before Connection#commit(), null
     * is returned.
     */
    public RowSet getResult()
    {
        return result;
    }

    /**
     * Sets the result on this Statement.
     *
     * @param result
     *         The result to set.
     */
    public void setResult(RowSet result)
    {
        this.result = result;
    }
}
