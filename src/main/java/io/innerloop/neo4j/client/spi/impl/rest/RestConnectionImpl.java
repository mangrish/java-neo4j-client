package io.innerloop.neo4j.client.spi.impl.rest;

import io.innerloop.neo4j.client.Connection;
import io.innerloop.neo4j.client.Graph;
import io.innerloop.neo4j.client.GraphStatement;
import io.innerloop.neo4j.client.Neo4jClientException;
import io.innerloop.neo4j.client.Neo4jServerException;
import io.innerloop.neo4j.client.Neo4jServerMultiException;
import io.innerloop.neo4j.client.RowSet;
import io.innerloop.neo4j.client.RowStatement;
import io.innerloop.neo4j.client.Statement;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONException;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONObject;
import io.innerloop.neo4j.client.spi.impl.rest.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * Created by markangrish on 12/12/2014.
 */
public class RestConnectionImpl implements Connection
{
    private static final Logger LOG = LoggerFactory.getLogger(RestConnectionImpl.class);

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z").withLocale(Locale.ENGLISH);

    private static ThreadLocal<RestConnectionImpl> connectionHolder = new ThreadLocal<>();

    public static Connection getConnection(HttpClient client, String transactionEndpointUrl)
    {
        RestConnectionImpl connection = connectionHolder.get();

        if (connection == null)
        {
            LOG.debug("Getting new Connection for Thread: [{}]", Thread.currentThread().getName());
            connection = new RestConnectionImpl(client, transactionEndpointUrl);
            connectionHolder.set(connection);
        }

        return connection;
    }

    public static void closeConnection()
    {
        connectionHolder.remove();
    }

    private final HttpClient client;

    private List<Statement> statements;

    private String activeTransactionEndpointUrl;

    private OffsetDateTime transactionExpires;

    public RestConnectionImpl(HttpClient client, String transactionEndpointUrl)
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
    public OffsetDateTime getExpiry()
    {
        return transactionExpires;
    }


    @Override
    public void flush()
    {
        try
        {
            LOG.debug("Flushing to [{}]", activeTransactionEndpointUrl);
            JSONObject jsonResult = execute(activeTransactionEndpointUrl);
            this.activeTransactionEndpointUrl = jsonResult.getString("commit").replace("/commit", "");
            this.transactionExpires = OffsetDateTime.parse(jsonResult.getJSONObject("transaction").getString("expires"),
                                                          FORMATTER);
            this.statements.clear();
            LOG.debug("Next endpoint is now: [{}] which expires at: [{}]",
                      activeTransactionEndpointUrl,
                      transactionExpires);

        }

        catch (IOException e)
        {
            close();
            throw new Neo4jClientException(e);
        }
        catch (JSONException e)
        {
            close();
            throw new Neo4jClientException("Error when processing JSON response.", e);
        }
    }

    @Override
    public void commit()
    {
        try
        {
            String commitEndpoint = activeTransactionEndpointUrl + "/commit";
            LOG.debug("Committing to [{}]", commitEndpoint);
            execute(commitEndpoint);
        }
        catch (Exception e)
        {
            throw new Neo4jClientException("Encountered an error when trying to commit to Neo4J. See exception for details.",
                                           e);
        }
        finally
        {
            LOG.debug("Closing connection.");
            close();
        }
    }

    @Override
    public void resetExpiry()
    {
        try
        {
            final JSONObject payload = new JSONObject().put("statements", new ArrayList<JSONObject>());
            LOG.info("Executing [{}] statements.", statements.size());
            LOG.debug("Statements are: [{}]", payload.toString());
            String result = client.post(activeTransactionEndpointUrl, payload);
            LOG.debug("Raw result is: [{}]", result);
            JSONObject jsonResult = new JSONObject(result);
            ExecutionResult er = new ExecutionResult(jsonResult);
            checkErrors(er.getErrors());
            this.activeTransactionEndpointUrl = jsonResult.getString("commit").replace("/commit", "");
            this.transactionExpires = OffsetDateTime.parse(jsonResult.getJSONObject("transaction").getString("expires"),
                                                          FORMATTER);
        }
        catch (Exception e)
        {
            close();
            throw new Neo4jClientException(e);
        }
    }


    private JSONObject execute(String endpointUrl) throws IOException
    {
        List<JSONObject> statements = this.statements.stream().map(Statement::toJson).collect(Collectors.toList());
        final JSONObject payload = new JSONObject().put("statements", (statements));
        LOG.info("Executing [{}] statements.", statements.size());
        LOG.debug("Statements are: [{}]", payload.toString());
        String result = client.post(endpointUrl, payload);
        LOG.debug("Raw result is: [{}]", result);
        JSONObject jsonResult = new JSONObject(result);
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
            String json = client.delete(activeTransactionEndpointUrl);
            ExecutionResult er = new ExecutionResult(new JSONObject(json));
            checkErrors(er.getErrors());
        }
        catch (Exception e)
        {
            close();
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
