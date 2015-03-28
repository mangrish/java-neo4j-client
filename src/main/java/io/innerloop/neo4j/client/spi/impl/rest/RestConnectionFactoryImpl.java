package io.innerloop.neo4j.client.spi.impl.rest;

import io.innerloop.neo4j.client.Connection;
import io.innerloop.neo4j.client.spi.ConnectionFactory;
import io.innerloop.neo4j.client.spi.impl.rest.http.HttpClient;

/**
 * Created by markangrish on 11/12/2014.
 */
public class RestConnectionFactoryImpl implements ConnectionFactory
{
    private final HttpClient client;

    private final String baseUrl;

    private final String transactionEndpointUrl;

    public RestConnectionFactoryImpl(String url)
    {
        this.client = new HttpClient();
        this.client.addHeader("X-Stream", "true");
        this.baseUrl = url.endsWith("/") ? url : url + "/";
        this.transactionEndpointUrl = this.baseUrl + "transaction";
    }

    public RestConnectionFactoryImpl(String url, String userName, String password)
    {
        this(url);
        client.authenticate(userName, password);
    }

    @Override
    public Connection getConnection()
    {
        return RestConnectionImpl.getConnection(client, transactionEndpointUrl);
    }

}
