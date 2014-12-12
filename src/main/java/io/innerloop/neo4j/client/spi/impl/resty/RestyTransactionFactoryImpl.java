package io.innerloop.neo4j.client.spi.impl.resty;

import io.innerloop.neo4j.client.Transaction;
import io.innerloop.neo4j.client.spi.TransactionFactory;
import io.innerloop.neo4j.client.spi.impl.resty.web.Resty;

/**
 * Created by markangrish on 11/12/2014.
 */
public class RestyTransactionFactoryImpl implements TransactionFactory
{
    private final Resty client;

    private final String baseUrl;

    private final String autoCommitEndpointUrl;

    private final String transactionEndpointUrl;

    public RestyTransactionFactoryImpl(String url)
    {
        this.client = new Resty();
        this.client.withHeader("X-Stream", "true");
        this.baseUrl = url.endsWith("/") ? url : url + "/";
        this.autoCommitEndpointUrl = this.baseUrl + "transaction/commit";
        this.transactionEndpointUrl = this.baseUrl + "transaction";
    }

    public RestyTransactionFactoryImpl(String url, String userName, String password)
    {
        this(url);
        client.authenticate(baseUrl, userName, password.toCharArray());
    }

    @Override
    public Transaction getAtomicTransaction()
    {
        return new RestyAtomicTransactionImpl(client, autoCommitEndpointUrl);
    }

    @Override
    public Transaction getLongTransaction()
    {
        return RestyLongTransactionImpl.getTransaction(client, transactionEndpointUrl);
    }

}
