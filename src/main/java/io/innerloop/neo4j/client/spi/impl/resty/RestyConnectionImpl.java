package io.innerloop.neo4j.client.spi.impl.resty;

import io.innerloop.neo4j.client.Connection;
import io.innerloop.neo4j.client.Graph;
import io.innerloop.neo4j.client.GraphStatement;
import io.innerloop.neo4j.client.Neo4jClientException;
import io.innerloop.neo4j.client.Neo4jServerException;
import io.innerloop.neo4j.client.Neo4jServerMultiException;
import io.innerloop.neo4j.client.RowSet;
import io.innerloop.neo4j.client.RowStatement;
import io.innerloop.neo4j.client.Statement;
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
public class RestyConnectionImpl implements Connection
{
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");

    private static ThreadLocal<RestyConnectionImpl> connectionHolder = new ThreadLocal<>();

    public static Connection getConnection(Resty client, String transactionEndpointUrl)
    {
        RestyConnectionImpl connection = connectionHolder.get();

        if (connection == null)
        {
            connection = new RestyConnectionImpl(client, transactionEndpointUrl);
            connectionHolder.set(connection);
        }

        return connection;
    }

    public static void closeConnection()
    {
        connectionHolder.remove();
    }

    private final Resty client;

    private List<Statement> statements;

    private String activeTransactionEndpointUrl;

    private LocalDateTime transactionExpires;

    public RestyConnectionImpl(Resty client, String transactionEndpointUrl)
    {
        this.activeTransactionEndpointUrl = transactionEndpointUrl;
        this.statements = new ArrayList<>();
        this.client = client;
    }


    @Override
    public void add(Statement statement)
    {
        this.statements.add(statement);
    }

    @Override
    public List<Statement> getStatements()
    {
        return statements;
    }

    @Override
    public LocalDateTime getExpiry()
    {
        return transactionExpires;
    }


    @Override
    public void flush()
    {
        try
        {
            JSONObject jsonResult = execute(activeTransactionEndpointUrl);
            this.activeTransactionEndpointUrl = jsonResult.getString("commit").replace("/commit", "");
            this.transactionExpires = LocalDateTime.parse(jsonResult.getJSONObject("transaction").getString("expires"),
                                                          FORMATTER);
            this.statements.clear();
        }

        catch (IOException e)
        {
            throw new Neo4jClientException(e);
        }
    }

    @Override
    public void commit()
    {
        try
        {
            execute(activeTransactionEndpointUrl + "/commit");
        }
        catch (Exception e)
        {
            throw new Neo4jClientException("Encountered an error when trying to commit to Neo4J. See exception for details.",
                                           e);
        }
        finally
        {
            close();
        }
    }

    @Override
    public void resetExpiry()
    {
        try
        {
            final JSONObject payload = new JSONObject().put("statements", new ArrayList<JSONObject>());
            JSONResource result = client.json(activeTransactionEndpointUrl, content(payload));
            JSONObject jsonResult = result.object();
            ExecutionResult er = new ExecutionResult(jsonResult);
            checkErrors(er.getErrors());
            this.activeTransactionEndpointUrl = jsonResult.getString("commit").replace("/commit", "");
            this.transactionExpires = LocalDateTime.parse(jsonResult.getJSONObject("transaction").getString("expires"),
                                                          FORMATTER);
        }

        catch (IOException e)
        {
            throw new Neo4jClientException(e);
        }
    }


    private JSONObject execute(String endpointUrl) throws IOException
    {
        List<JSONObject> statements = this.statements.stream().map(Statement::toJson).collect(Collectors.toList());
        final JSONObject payload = new JSONObject().put("statements", (statements));
        JSONResource result = client.json(endpointUrl, content(payload));
        JSONObject jsonResult = result.object();
        ExecutionResult er = new ExecutionResult(jsonResult);
        checkErrors(er.getErrors());
        for (int i = 0; i < this.statements.size(); i++)
        {
            Statement statement = this.statements.get(i);
            JSONObject jsonObject = er.geResult(i);
            if (statement.getType().equals("row"))
            {
                RowSet rs = er.buildRowSet(jsonObject);
                ((RowStatement) statement).setResult(rs);
            }
            else
            {
                Graph g = er.buildGraph(jsonObject);
                ((GraphStatement) statement).setResult(g);
            }
        }
        return jsonResult;
    }

    @Override
    public void rollback()
    {
        try
        {
            JSONResource json = client.json(activeTransactionEndpointUrl, delete());
            ExecutionResult er = new ExecutionResult(json.object());
            checkErrors(er.getErrors());
        }
        catch (IOException e)
        {
            throw new Neo4jClientException(e);
        }
    }

    private void close()
    {
        closeConnection();
    }

    void checkErrors(Neo4jServerException[] exceptions)
    {
        int length = exceptions.length;

        if (length == 1)
        {
            throw exceptions[0];
        }

        if (length > 1)
        {
            throw new Neo4jServerMultiException("Multiple errors occurred when executing statements", exceptions);
        }
    }
}
