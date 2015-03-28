package io.innerloop.neo4j.client.spi.impl.rest.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an HTTP Message, which could either be a Request or a Response.  Message is an abstract class
 * that contains fields and methods that are common to both types of Messages.
 *
 * @param <T>
 *         A Type that extends Message (either {@link Request} or {@link Response})
 */
abstract class Message<T extends Message<T>>
{
    protected Map<String, List<String>> headers = new HashMap<>();

    protected String body;

    /**
     * The default constructor is a no-op constructor.
     */
    public Message()
    {
        // No-arg, No-op constructor
    }

    /**
     * Returns the Message body (also known as the Entity body).
     *
     * @return The body of the HTTP Message.  It will typically be HTML, JSON, or XML.
     */
    public String getBody()
    {
        return this.body;
    }

    /**
     * Sets the body of the Message.
     *
     * @param body
     *         This is typically the JSON, XML, or Form Parameters being sent to the server.
     *
     * @return this Message, to support chained method calls
     */
    @SuppressWarnings("unchecked")
    public T setBody(String body)
    {
        this.body = body;
        return (T) this;
    }

    /**
     * Adds a single header value to the Message.
     *
     * @param name
     *         The header name.
     * @param value
     *         The header value
     *
     * @return this Message, to support chained method calls
     */
    @SuppressWarnings("unchecked")
    public T addHeader(String name, String value)
    {
        List<String> values = new ArrayList<String>();
        values.add(value);

        this.headers.put(name, values);
        return (T) this;
    }

    /**
     * Removes the specified header.
     *
     * @param name
     *         The name of the header to remove.
     *
     * @return this Message, to support chained method calls
     */
    @SuppressWarnings("unchecked")
    public T removeHeader(String name)
    {
        this.headers.remove(name);
        return (T) this;
    }

    /**
     * Sets all of the headers in one call.
     *
     * @param headers
     *         A Map of headers, where the header name is a String, and the value is a List of one or more values.
     *
     * @return this Message, to support chained method calls
     */
    @SuppressWarnings("unchecked")
    public T setHeaders(Map<String, List<String>> headers)
    {
        this.headers = headers;
        return (T) this;
    }


}
