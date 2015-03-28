package io.innerloop.neo4j.client.spi.impl.rest.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an HTTP Request message.
 */
public class Request extends Message<Request>
{
    private HttpURLConnection connection;

    private OutputStreamWriter writer;

    private URL url;

    private Map<String, String> query = new HashMap<>();

    /**
     * The Constructor takes the url as a String.
     *
     * @param url
     *         The url parameter does not need the query string parameters if they are going to be supplied via calls to
     *         {@link #addQueryParameter(String, String)}.  You can, however, supply the query parameters in the URL if
     *         you wish.
     *
     * @throws IOException
     */
    public Request(String url) throws IOException
    {
        this.url = new URL(url);

        connection = (HttpURLConnection) this.url.openConnection();
    }

    /**
     * Adds a Query Parameter to a list.  The list is converted to a String and appended to the URL when the Request is
     * submitted.
     *
     * @param name
     *         The Query Parameter's name
     * @param value
     *         The Query Parameter's value
     *
     * @return this Request, to support chained method calls
     */
    public Request addQueryParameter(String name, String value)
    {
        this.query.put(name, value);
        return this;
    }

    /**
     * Removes the specified Query Parameter.
     *
     * @param name
     *         The name of the Query Parameter to remove
     *
     * @return this Request, to support chained method calls
     */
    public Request removeQueryParameter(String name)
    {
        this.query.remove(name);
        return this;
    }

    /**
     * Sets the URL that this Request will be sent to.
     *
     * @param url The url parameter does not need the query string parameters if
     *            they are going to be supplied via calls to {@link #addQueryParameter(String, String)}.  You can, however, supply
     *            the query parameters in the URL if you wish.
     * @return this Request, to support chained method calls
     * @throws MalformedURLException If the supplied url is malformed.
     */
    //    public Request setUrl(String url) throws MalformedURLException {
    //        this.url = new URL(url);
    //        return this;
    //    }

    /**
     * Issues a GET to the server.
     *
     * @return The {@link Response} from the server
     *
     * @throws IOException
     */
    public Response getResource() throws IOException
    {
        buildQueryString();
        buildHeaders();

        connection.setDoOutput(true);
        connection.setRequestMethod("GET");

        return readResponse();
    }

    /**
     * Issues a PUT to the server.
     *
     * @return The {@link Response} from the server
     *
     * @throws IOException
     */
    public Response putResource() throws IOException
    {
        return writeResource("PUT", this.body);
    }

    /**
     * Issues a POST to the server.
     *
     * @return The {@link Response} from the server
     *
     * @throws IOException
     */
    public Response postResource() throws IOException
    {
        return writeResource("POST", this.body);
    }

    /**
     * Issues a DELETE to the server.
     *
     * @return The {@link Response} from the server
     *
     * @throws IOException
     */
    public Response deleteResource() throws IOException
    {
        buildQueryString();
        buildHeaders();

        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");

        return readResponse();
    }

    /**
     * A private method that handles issuing POST and PUT requests
     *
     * @param method
     *         POST or PUT
     * @param body
     *         The body of the Message
     *
     * @return the {@link Response} from the server
     *
     * @throws IOException
     */
    private Response writeResource(String method, String body) throws IOException
    {
        buildQueryString();
        buildHeaders();

        connection.setDoOutput(true);
        connection.setRequestMethod(method);

        writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body);
        writer.close();

        return readResponse();
    }

    /**
     * A private method that handles reading the Responses from the server.
     *
     * @return a {@link Response} from the server.
     *
     * @throws IOException
     */
    private Response readResponse() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
        {
            builder.append(line);
        }
        reader.close();

        return new Response().setResponseCode(connection.getResponseCode())
                       .setResponseMessage(connection.getResponseMessage())
                       .setHeaders(connection.getHeaderFields())
                       .setBody(builder.toString());
    }

    /**
     * A private method that loops through the query parameter Map, building a String to be appended to the URL.
     *
     * @throws MalformedURLException
     */
    private void buildQueryString() throws MalformedURLException
    {
        StringBuilder builder = new StringBuilder();

        // Put the query parameters on the URL before issuing the request
        if (!query.isEmpty())
        {
            for (Map.Entry param : query.entrySet())
            {
                builder.append(param.getKey());
                builder.append("=");
                builder.append(param.getValue());
                builder.append("&");
            }
            builder.deleteCharAt(builder.lastIndexOf("&")); // Remove the trailing ampersand
        }

        if (builder.length() > 0)
        {
            // If there was any query string at all, begin it with the question mark
            builder.insert(0, "?");
        }

        url = new URL(url.toString() + builder.toString());
    }

    /**
     * A private method that loops through the headers Map, putting them on the Request or Response object.
     */
    private void buildHeaders()
    {
        if (!headers.isEmpty())
        {
            for (Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                List<String> values = entry.getValue();

                for (String value : values)
                {
                    connection.addRequestProperty(entry.getKey(), value);
                }
            }
        }

    }

}
