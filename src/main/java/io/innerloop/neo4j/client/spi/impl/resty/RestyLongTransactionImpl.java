package io.innerloop.neo4j.client.spi.impl.resty;

import io.innerloop.neo4j.client.Neo4jClientException;
import io.innerloop.neo4j.client.Neo4jClientMultiException;
import io.innerloop.neo4j.client.Statement;
import io.innerloop.neo4j.client.Transaction;
import io.innerloop.neo4j.client.json.JSONObject;
import io.innerloop.neo4j.client.spi.impl.resty.web.JSONResource;
import io.innerloop.neo4j.client.spi.impl.resty.web.Resty;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.innerloop.neo4j.client.spi.impl.resty.web.Resty.content;
import static io.innerloop.neo4j.client.spi.impl.resty.web.Resty.delete;

/**
 * Created by markangrish on 12/12/2014.
 */
public class RestyLongTransactionImpl implements Transaction
{
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");

    private static ThreadLocal<RestyLongTransactionImpl> activeTransactions = new ThreadLocal<>();

    public static Transaction getTransaction(Resty client, String transactionEndpointUrl)
    {
        RestyLongTransactionImpl transaction = activeTransactions.get();

        if (transaction == null)
        {
            transaction = new RestyLongTransactionImpl(client, transactionEndpointUrl);
            activeTransactions.set(transaction);
        }

        return transaction;
    }

    public static void closeTransaction()
    {
        activeTransactions.set(null);
    }

    private final Resty client;

    private List<Statement> statements;

    private boolean begun;

    private String activeTransactionEndpointUrl;

    private LocalDateTime transactionExpires;

    public RestyLongTransactionImpl(Resty client, String transactionEndpointUrl)
    {
        this.begun = false;
        this.activeTransactionEndpointUrl = transactionEndpointUrl;
        this.statements = new ArrayList<>();
        this.client = client;
    }

    @Override
    public void begin()
    {
        if (begun)
        {
            throw new RuntimeException("Transaction has already begun");
        }
        this.begun = true;
    }

    @Override
    public void add(Statement statement)
    {
        this.statements.add(statement);
    }

    @Override
    public void flush() throws Neo4jClientException
    {
        List<JSONObject> statements = this.statements.stream().map(Statement::toJson).collect(Collectors.toList());
        final JSONObject payload = new JSONObject().put("statements", (statements));
        try
        {
            JSONResource result = client.json(activeTransactionEndpointUrl, content(payload));
            JSONObject jsonResult = result.object();
            ExecutionResult er = new ExecutionResult(jsonResult);
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
            this.activeTransactionEndpointUrl = jsonResult.getString("commit").replace("/commit", "");
            this.transactionExpires = LocalDateTime.parse(jsonResult.getJSONObject("transaction").getString("expires"), FORMATTER);
            this.statements.clear();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() throws Neo4jClientException
    {
        List<JSONObject> statements = this.statements.stream().map(Statement::toJson).collect(Collectors.toList());
        final JSONObject payload = new JSONObject().put("statements", (statements));
        try
        {
            JSONResource result = client.json(activeTransactionEndpointUrl + "/commit", content(payload));
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
    public void rollback() throws Neo4jClientException
    {
        try
        {
            JSONResource json = client.json(activeTransactionEndpointUrl, delete());
            ExecutionResult er = new ExecutionResult(json.object());
            checkErrors(er.getErrors());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()
    {
        closeTransaction();
    }

    void checkErrors(Neo4jClientException[] exceptions) throws Neo4jClientException
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
