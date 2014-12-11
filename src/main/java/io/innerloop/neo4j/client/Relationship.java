package io.innerloop.neo4j.client;

import java.util.Map;

/**
 * Represents a Neo4J Relationship. <p>Relationships connect nodes and are comprised of an id, type and start/end node
 * id's.</p> <p> <p>Again, similar to Nodes, relationship id's <strong>SHOULD NOT BE USED</strong> to identify a
 * relationship.</p>
 */
public class Relationship
{
    private final long id;

    private final long endNodeId;

    private final long startNodeId;

    private final String type;

    private final Map<String, Object> properties;

    /**
     * Creates a new relationship to connect two Nodes.
     *
     * @param id
     *         The Neo4J Specific id for this relationship. Do not use this as a real identifier outside of the scope of
     *         this request.
     * @param type
     *         The type of this relationship. Usually a verb type name.
     * @param startNodeId
     *         The first node that this relationship connects. That is the direction originates from here.
     * @param endNodeId
     *         THe end node that this relationship connects. The direction terminates here.
     * @param properties
     *         Like Nodes, Relationships can store properties.
     */
    public Relationship(long id, String type, long startNodeId, long endNodeId, Map<String, Object> properties)
    {
        this.id = id;
        this.type = type;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.properties = properties;
    }

    /**
     * Returns this relationship's id. DO NOT USE AS AN IDENTITY BEYOND THE SCOPE OF THIS GRAPH RESULT.
     *
     * @return a long integer representing this relationship.
     */
    public long getId()
    {
        return id;
    }

    /**
     * Retrieves the relationships end node id.
     *
     * @return The end node's id which this relationship connects.
     */
    public long getEndNodeId()
    {
        return endNodeId;
    }

    /**
     * Retrieves the relationships origin node id.
     *
     * @return The first node's id which this relationship connects.
     */
    public long getStartNodeId()
    {
        return startNodeId;
    }

    /**
     * Returns this node's type. Relationships always have a type.
     *
     * @return The type of this relationship. Usually a verb type name.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Returns a map of properties stored in this Relationship.
     *
     * @return a map of properties.
     */
    public Map<String, Object> getProperties()
    {
        return properties;
    }
}
