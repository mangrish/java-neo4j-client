package io.innerloop.neo4j.client;

import java.util.Map;

/**
 * Represents a Neo4J Node. <p> <p>Nodes are comprised of an id, labels and primitive/String properties.</p> <p> <p>Node
 * id's <strong>SHOULD NOT BE USED</strong>. THe Neo4J Server can reclaim id's. Id's are only provided to connect nodes
 * to other nodes via <code>Relationship</code>'s. Relationships connect nodes by their id's but can be thought of as
 * transient and only consistent with the whole graph itself.</p> <p> <p>At this stage anything that can be deciphered
 * by the JSON library will be held as a property.</p>
 */
public class Node
{
    private final long id;

    private final String[] labels;

    private final Map<String, Object> properties;

    /**
     * Creates a new Node.
     * <p>
     * <p>Note: Because this api's default implementation is known there is no safe constructor copying.
     *
     * @param id
     *         The id of this node.
     * @param labels
     *         An array of strings that label this Node.
     * @param properties
     *         The properties stored in this node.
     */
    public Node(long id, String[] labels, Map<String, Object> properties)
    {
        this.id = id;
        this.labels = labels;
        this.properties = properties;
    }

    /**
     * Returns this node's id. DO NOT USE AS AN IDENTITY BEYOND THE SCOPE OF THIS GRAPH RESULT.
     *
     * @return a long integer representing this node.
     */
    public long getId()
    {
        return id;
    }

    /**
     * Returns this node's labels.
     *
     * @return an array of strings. Can return null.
     */
    public String[] getLabels()
    {
        return this.labels;
    }

    /**
     * Returns a map of properties stored in this Node.
     *
     * @return a map of properties.
     */
    public Map<String, Object> getProperties()
    {
        return this.properties;
    }
}
