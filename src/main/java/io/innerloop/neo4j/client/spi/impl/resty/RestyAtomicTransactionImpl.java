package io.innerloop.neo4j.client.spi.impl.resty;

import io.innerloop.neo4j.client.Neo4jClientException;
import io.innerloop.neo4j.client.Neo4jClientMultiException;
import io.innerloop.neo4j.client.Statement;
import io.innerloop.neo4j.client.Transaction;
import io.innerloop.neo4j.client.json.JSONObject;
import io.innerloop.neo4j.client.spi.impl.resty.web.JSONResource;
import io.innerloop.neo4j.client.spi.impl.resty.web.Resty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.innerloop.neo4j.client.spi.impl.resty.web.Resty.content;

/**
 * Created by markangrish on 11/12/2014.
 */
public class RestyAtomicTransactionImpl implements Transaction
{
    private final List<Statement> statements;

    private final Resty client;

    private final String autoCommitEndpointUrl;

    public RestyAtomicTransactionImpl(Resty client, String autoCommitEndpointUrl)
    {
        this.client = client;
        this.autoCommitEndpointUrl = autoCommitEndpointUrl;
        this.statements = new ArrayList<>();
    }

    @Override
    public void begin()
    {
        throw new UnsupportedOperationException("Long Transactions not currently supported by this driver.");
    }

    @Override
    public void add(Statement statement)
    {
        this.statements.add(statement);
    }

    @Override
    public void flush()
    {
        throw new UnsupportedOperationException("Long Transactions not currently supported by this driver.");
    }

    @Override
    public void commit()
    {
        List<JSONObject> statements = this.statements.stream().map(Statement::toJson).collect(Collectors.toList());
        final JSONObject payload = new JSONObject().put("statements", (statements));
        try
        {
            JSONResource result = client.json(autoCommitEndpointUrl, content(payload));
            ExecutionResult er = new ExecutionResult(result.object());
            checkErrors(er.getErrors());
            for (int i = 0; i < this.statements.size(); i++)
            {
                Statement statement = this.statements.get(i);
                JSONObject result1 = er.geResult(i);
                if (statement.getType().equals("row"))
                {
                    statement.setResult(er.buildRowSet(result1));
                }
                else
                {
                    statement.setResult(er.buildGraph(result1));
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void rollback()
    {
        this.statements.clear();
    }

    @Override
    public void close()
    {
        //do nothing.
    }

    void checkErrors(Neo4jClientException[] exceptions)
    {
        int length = exceptions.length;

        if (length == 1)
        {
            throw exceptions[0];
        }

        if (length > 1)
        {
            throw new Neo4jClientMultiException("Multiple errors occurred when executing statements", exceptions);
        }
    }
}
