package io.innerloop.neo4j.client;

import java.util.Map;

/**
 * Created by markangrish on 24/10/2014.
 */
public class Relationship
{
    private final long id;

    private final long endNodeId;

    private final long startNodeId;

    private final String type;

    private final Map<String, Object> properties;

    public Relationship(long id, String type, long startNodeId, long endNodeId, Map<String, Object> properties)
    {
        this.id = id;
        this.type = type;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.properties = properties;
    }

    public long getId()
    {
        return id;
    }

    public long getEndNodeId()
    {
        return endNodeId;
    }

    public long getStartNodeId()
    {
        return startNodeId;
    }

    public String getType()
    {
        return type;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }
}
