package io.innerloop.neo4j.client.spi.impl.rest;

import io.innerloop.neo4j.client.Graph;
import io.innerloop.neo4j.client.Neo4jServerException;
import io.innerloop.neo4j.client.Node;
import io.innerloop.neo4j.client.Relationship;
import io.innerloop.neo4j.client.RowSet;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONArray;
import io.innerloop.neo4j.client.spi.impl.rest.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by markangrish on 06/11/2014.
 */
public class ExecutionResult
{
    private final JSONObject response;

    public ExecutionResult(JSONObject response)
    {
        this.response = response;
    }

    public Neo4jServerException[] getErrors()
    {
        JSONArray errorsJson = response.getJSONArray("errors");
        int length = errorsJson.length();

        Neo4jServerException[] errors = new Neo4jServerException[length];

        for (int i = 0; i < length; i++)
        {
            JSONObject errorJson = errorsJson.getJSONObject(i);
            errors[i] = new Neo4jServerException(errorJson.optString("code"), errorJson.optString("message"));
        }

        return errors;
    }


    public JSONObject geResult(int index)
    {
        JSONArray results = response.getJSONArray("results");
        return results.getJSONObject(index);
    }

    public Graph buildGraph(JSONObject result)
    {
        Map<Long, Node> seenNodes = new HashMap<>();
        Map<Long, Relationship> seenRelationships = new HashMap<>();

        JSONArray data = result.getJSONArray("data");
        int dataLength = data.length();

        for (int i = 0; i < dataLength; i++)
        {
            JSONObject datum = data.getJSONObject(i);
            JSONObject graph = datum.getJSONObject("graph");
            JSONArray nodes = graph.getJSONArray("nodes");
            int nodesLength = nodes.length();

            for (int j = 0; j < nodesLength; j++)
            {
                JSONObject node = nodes.getJSONObject(j);
                long id = node.getLong("id");
                Node n = seenNodes.get(id);

                if (n != null)
                {
                    continue;
                }

                JSONArray labels = node.getJSONArray("labels");
                int labelsLength = labels.length();
                String[] ls = new String[labelsLength];

                for (int k = 0; k < labelsLength; k++)
                {
                    ls[k] = labels.getString(k);
                }

                JSONObject properties = node.getJSONObject("properties");
                Map<String, Object> ps = JsonUtils.jsonToMap(properties);

                n = new Node(id, ls, ps);
                seenNodes.put(id, n);
            }

            JSONArray relationships = graph.getJSONArray("relationships");
            int relationshipsLength = relationships.length();

            for (int j = 0; j < relationshipsLength; j++)
            {
                JSONObject relationship = relationships.getJSONObject(j);
                long id = relationship.getLong("id");
                long startNode = relationship.getLong("startNode");
                long endNode = relationship.getLong("endNode");
                String type = relationship.getString("type");
                JSONObject properties = relationship.getJSONObject("properties");
                Map<String, Object> ps = JsonUtils.jsonToMap(properties);

                Relationship r = new Relationship(id, type, startNode, endNode, ps);
                seenRelationships.put(r.getId(), r);
            }
        }
        Set<Node> ns = new HashSet<>(seenNodes.values());
        Set<Relationship> rs = new HashSet<>(seenRelationships.values());

        return new Graph(ns, rs);
    }

    public RowSet buildRowSet(JSONObject result)
    {
        JSONArray columnNamesJson = result.getJSONArray("columns");
        int columnsLength = columnNamesJson.length();

        List<String> columnNames = new ArrayList<>();

        for (int i = 0; i < columnsLength; i++)
        {
            columnNames.add(columnNamesJson.getString(i));
        }

        List<Object[]> rows = new ArrayList<>();

        JSONArray data = result.getJSONArray("data");
        int dataLength = data.length();


        for (int i = 0; i < dataLength; i++)
        {
            JSONObject datum = data.getJSONObject(i);
            JSONArray row = datum.getJSONArray("row");

            int rowLength = row.length();
            Object[] r = new Object[columnsLength];
            rows.add(r);

            for (int j = 0; j < rowLength; j++)
            {
                Object object;

                JSONObject jsonObject = row.optJSONObject(j);
                if (jsonObject != null)
                {
                    object = JsonUtils.jsonToMap(jsonObject);
                    r[j] = object;
                    continue;
                }
                object = row.opt(j);
                if (object != null)
                {
                    r[j] = object;
                }
            }
        }

        return new RowSet(columnNames.toArray(new String[columnNames.size()]), rows);
    }
}
