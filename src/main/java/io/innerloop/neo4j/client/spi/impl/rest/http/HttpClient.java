package io.innerloop.neo4j.client.spi.impl.rest.http;

import io.innerloop.neo4j.client.spi.impl.rest.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markangrish on 28/03/2015.
 */
public class HttpClient
{
    private final Map<String, String> headers;

    public HttpClient()
    {
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value)
    {
        headers.put(name, value);
    }

    public String post(String endpoint, JSONObject payload) throws IOException
    {
        Request request = new Request(endpoint);
        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            request.addHeader(entry.getKey(), entry.getValue());
        }

        Response httpResponse = request.addHeader("Content-Type", "application/json")
                                        .setBody(payload.toString())
                                        .postResource();
        return httpResponse.getBody();
    }

    public String delete(String endpoint) throws IOException
    {
        Request request = new Request(endpoint);
        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            request.addHeader(entry.getKey(), entry.getValue());
        }
        Response httpResponse = request.deleteResource();

        return httpResponse.getBody();
    }

    public void authenticate(String username, String password)
    {
        String userPassword = username + ":" + password;
        String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
        addHeader("Authorization", "Basic " + encoding);
    }
}
