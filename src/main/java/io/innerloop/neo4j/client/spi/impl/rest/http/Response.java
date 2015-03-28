package io.innerloop.neo4j.client.spi.impl.rest.http;

import java.util.List;
import java.util.Map;

/**
 * This class represents an HTTP Response message.
 */
public class Response extends Message<Response>
{

    private int responseCode;

    private String responseMessage;

    /**
     * The default constructor is a no-op
     */
    public Response()
    {
        // no-op
    }

    /**
     * Gets the HTTP Response Code from this Response instace.
     *
     * @return The HTTP Response Code that was sent from the server
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * Sets the HTTP Response Code on this object.
     *
     * @param responseCode
     *         Any of the standard HTTP Response Codes
     *
     * @return This object, to support chained method calls
     */
    public Response setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
        return this;
    }

    /**
     * Returns a message pertaining to the Response Code.
     *
     * @return Any response message that may have been returned by the server.  This message should be related to the
     * and should not be confused with the Response Body.
     */
    public String getResponseMessage()
    {
        return responseMessage;
    }

    /**
     * Sets the Response Message, which should pertain to the Response Code
     *
     * @param responseMessage
     *         Any message which was sent back from the server, pertaining to the Response Code
     *
     * @return this Response, to support chained method calls
     */
    public Response setResponseMessage(String responseMessage)
    {
        this.responseMessage = responseMessage;
        return this;
    }

    /**
     * Returns a String representation of this Response.  Helpful for debugging.
     *
     * @return Returns a String representation of this Response.  Helpful for debugging.
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        String newline = System.getProperty("line.separator");

        builder.append("Response Code: ")
                .append(this.responseCode)
                .append(newline)
                .append("Response Message: ")
                .append(newline)
                .append(newline)
                .append("Headers: ")
                .append(newline);

        for (Map.Entry<String, List<String>> entry : headers.entrySet())
        {
            List<String> values = entry.getValue();
            for (String value : values)
            {
                builder.append(entry.getKey()).append(" = ").append(value).append(newline);
            }
        }

        builder.append(newline).append("Body: ").append(newline).append(body);

        return builder.toString();
    }
}
