package io.innerloop.neo4j.client;

/**
 * This is a statement that returns graphs.
 *
 * @see io.innerloop.neo4j.client.Graph
 * @see io.innerloop.neo4j.client.Statement
 */
public class GraphStatement extends Statement
{
    private Graph result;

    public GraphStatement(String query)
    {
        super(query);
    }

    @Override
    public String getType()
    {
        return "graph";
    }

    /**
     * Retrieves the result of this Statement. Only accessible after Connection#commit() has been called.
     *
     * @return The result of the execution of this statement if available. If called before Connection#commit(), null
     * is returned.
     */
    public Graph getResult()
    {
        return result;
    }

    /**
     * Sets the result on this Statement.
     *
     * @param result
     *         The result to set.
     */
    public void setResult(Graph result)
    {
        this.result = result;
    }

}
