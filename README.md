java-neo4j-client
=================

A compact Java driver that supports Graphs natively for standalone Neo4J instances.

# Features

1. No 3rd party dependencies.
1. Very simple, Neo4J specific API that only uses Cypher, the Neo4J query language.
1. Support for Graph and Row based result retrieval.
1. Transactions can be executed using Atomic or traditional "Long" Transactions.
1. Allows multiple queries per Transaction. Useful for batching or realising patterns like Unit of Work.


# Usage

## Install

To install from Maven:

```maven
<dependency>
    <groupId>io.innerloop</groupId>
    <artifactId>java-neo4j-client</artifactId>
    <version>0.1.1</version>
</dependency>
```

To install from Gradle:

```gradle
compile group: 'io.innerloop', name: 'java-neo4j-client', version: '0.1.1'
```

## Initialise

Initialise the driver like so:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");
```

You will only need one of these instances per application. This object is thread safe so feel free to share it.
If you are dealing with a password protected Neo4J instance there is an overloaded constructor that will serve you.


## Create a Transaction and add some Statements

java-neo4j-driver Transactions are a little like JDBC Connections and Transactions merged together.

All Neo4J Queries must run within a Transaction and this driver gives you two options.

1. _Atomic Transactions_: Similar to JDBC "auto commit" Statements except you can add as many statements as you like to
it. This is an all or nothing type transactions. Either all Statements succeed or none at all. No need to begin or
rollback transactions.
1. _"Long" Transactions_: The classic database transaction model with one major difference: batches of statements can be
flushed to the database intermittently before being committed. It also provides the capability to rollback Transactions.


Statements also come in two flavours:

1. _Graph Statements_: Will return results in a graph format. Useful when you want to visualise your graph or map it to
domain objects etc.
1. _Row based Statements_: Will return results in a more JDBC familiar table format, with column names and rows of data.
Useful if you want to get aggregate results or perform more relational type queries.

Statements also support parameter replacement. Simply use a placeholder in your cypher query like so: ```{ placeholder }```.

In order to do Graph queries you will need to return relationships, not just the nodes!

RowSets are just stripped down versions of the JDBC ResultSet. You can iterate through RowSets using the next() method.

### Atomic Transactions

Here is an example of using an Atomic Transaction:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");

Transaction transaction = client.getAtomicTransaction();
Statement<Graph> statement = null;
try {
    statement = new GraphStatement("MATCH (n:Label{uuid:{ uuid }})-[rels]-() RETURN n, rels")
    statement.addParam("uuid", "abcd1234"); // replace the uuid placeholder

    transaction.add(statement);

    transaction.commit();
}
catch (Neo4jClientException nce) {
    // handle exception.
}

Graph graph = statement.getResult(); // Do your Graph stuff here!
```


### Long Transactions

And here is an example of using a Long Transaction:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");

Transaction transaction = client.getLongTransaction(); // gets the active long transaction 
try
{
    transaction1.begin();

    Statement<RowSet> statement1 = new RowStatement("MERGE (n1:Graph{id:\"id1\", prop1:\"property1\"})-[:connectedTo]-(n2:Graph{id:\"id2\", prop1:\"property2\"})");
    Statement<RowSet> statement2 = new RowStatement("MERGE (n2:Graph{id:\"id3\", prop1:\"property3\"})");

    transaction.add(statement1);
    transaction.add(statement2);
    transaction.flush(); // this will execute any statements that have already appeared. Writes are isolated to this Transaction

    Statement<RowSet> statement3 = new RowStatement("MERGE (n2:Graph{id:\"id4\"}) SET n2 = {props}");
    Map<String, Object> props = new HashMap<>();
    props.put("id", "id4");
    props.put("prop1", "property4");
    props.put("random", 213);
    statement3.setParam("props", props);

    transaction.add(statement3);
    transaction.commit();
}
catch (Neo4jClientException e)
{
    try
    {
        transaction1.rollback();
    }
    catch (Neo4jClientException e1)
    {
        throw new RuntimeException(e);
    }
}

```

This is a more complicated example. It shows 2 statements being created and flushed to the database. If another transaction
tried to read the written data at this stage it would not be visible to them (until commit() is called).

This example also shows how you can insert a whole node just using a Map and placeholder replacement in the query.


# Why another Driver?

There are a few Java Neo4J Drivers out there, the two most used being the neo4j-rest-binding, a relic from the Neo4J 1.x
days and the much newer neo4j-jdbc-driver.

As much as the jdbc driver is a big move forward, it suffers from some major issues:

1. It shoehorns the power of Neo4J's unique graph ability into a relational databases model which just doesn't fit that
nicely with the Graph world.
1. The Neo4J jdbc driver does also not support Neo4J's "graph" mode. Graph mode massively reduces the response payload and
it allows users to store and retrieve object graphs, natively. This is so amazing but cannot be done with the JDBC driver AT ALL.
1. It's not lightweight. Users get to add a tonne of dependencies that may conflict with their own dependencies.

I wanted something that allows me to query Neo4J in the form that makes it so powerful: the graph; but still have the ability
to fall back on row querying when i needed it. Think of this driver as the Java equivalent of of the Neo4j Web Browser.

The API deliberately steers clear of JDBC conventions as Neo4J's usage is different enough that trying to make it play
with that style is more work than it's worth.


# More Examples

Return a graph and how many nodes are labelled with a certain Label.

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");

Transaction transaction = client.getAtomicTransaction();

Statement<Graph> statement1 = new GraphStatement("MATCH (n:Label{uuid:{uuid}})-[rels]-() RETURN n, rels")
statement1.addParam("uuid", "abcd1234");

Statement<RowSet> statement2 = new RowStatement("MATCH (n:Label) RETURN count(n)")

transaction.add(statement1);
transaction.add(statement2);

transaction.commit();

Graph graph = statement1.getResult(); // Do your Graph stuff here!

RowSet rowSet = statement2.getResult();
int count = rowSet.getInt(0); // you get the idea!
```


# Roadmap

- Clean up API to make it a bit more friendly.
- I'm not happy with the Statement API and that will probably change to be more of a builder (e.g. new Statement(query).resultType(GRAPH).includeStats(true) etc.)
