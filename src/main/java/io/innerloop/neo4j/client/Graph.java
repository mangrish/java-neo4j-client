package io.innerloop.neo4j.client;


import java.util.Set;

/**
 * Represents a graph comprised on <code>Node</code>'s and <code>Relationship</code>'s. <p> <p> Note that this is not a
 * directed graph and cannot be walked. Rather it is expected that Nodes and Relationships are normalised from the Neo4J
 * format to the user's desired data structure. </p>
 */
public class Graph
{
    private final Set<Node> nodes;

    private final Set<Relationship> relationships;

    /**
     * Creates a new Graph.
     *
     * <p>Note: Because this api's default implementation is known there is no safe constructor copying.
     *
     * @param nodes The set of Nodes in this graph.
     *
     * @param relationships The set of Relationships in this Graph.
     */
    public Graph(Set<Node> nodes, Set<Relationship> relationships)
    {
        this.nodes = nodes;
        this.relationships = relationships;
    }

    /**
     * Retrieves an iterable collection of <code>Node</code>'s.
     *
     * @return a collection of nodes. If there are no nodes an empty result is returned.
     */
    public Iterable<Node> getNodes()
    {
        return nodes;
    }

    /**
     * Retrieves an iterable collection of <code>Relationship</code>'s that connect the nodes in this graph.
     *
     * @return a collection of relationships. If there are no nodes an empty result is returned.
     */
    public Iterable<Relationship> getRelationships()
    {
        return relationships;
    }
}
