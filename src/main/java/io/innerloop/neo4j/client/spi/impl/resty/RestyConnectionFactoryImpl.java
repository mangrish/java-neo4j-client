package io.innerloop.neo4j.client.spi.impl.resty;

import io.innerloop.neo4j.client.Connection;
import io.innerloop.neo4j.client.spi.ConnectionFactory;
import io.innerloop.neo4j.client.spi.impl.resty.web.Resty;

/**
 * Created by markangrish on 11/12/2014.
 */
public class RestyConnectionFactoryImpl implements ConnectionFactory
{
    private final Resty client;

    private final String baseUrl;

    private final String transactionEndpointUrl;

    public RestyConnectionFactoryImpl(String url)
    {
        this.client = new Resty();
        this.client.withHeader("X-Stream", "true");
        this.baseUrl = url.endsWith("/") ? url : url + "/";
        this.transactionEndpointUrl = this.baseUrl + "transaction";
    }

    public RestyConnectionFactoryImpl(String url, String userName, String password)
    {
        this(url);
        client.authenticate(baseUrl, userName, password.toCharArray());
    }

    @Override
    public Connection getConnection()
    {
        return RestyConnectionImpl.getConnection(client, transactionEndpointUrl);
    }

}
