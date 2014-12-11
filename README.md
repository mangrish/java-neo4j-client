java-neo4j-client
=================

A compact Java driver that supports Graphs natively for standalone Neo4J instances.

# Background

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


# Usage

Typically, you will only need to instantiate the Neo4J driver once like so:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");
```

You can also pass in a username and password via an overloaded constructor if you need that capability.

The API deliberately steers clear of JDBC conventions as Neo4J's usage is different enough that trying to make it play
with that style is more work than it's worth.

So once you have the client created you can now grab a new Transaction.

```java
Transaction transaction = client.getAtomicTransaction();
```

Transactions aren't like what you are used to with JDBC. Transactions are more like JDBC Connections. The main difference
is that you can add as many Statements as you like to a Transaction before committing it. Once a Transaction is committed,
results are available on the statements themselves.

A typical usage might look like this:

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

In order to do Graph queries you will need to return relationships, not just the nodes!

RowSets are just stripped down versions of the JDBC ResultSet. You can iterate through RowSets using the next() method.

Just a note. I'm not happy with the Statement API and that will probably change to be more of a builder (e.g. new Statement(query).resultType(GRAPH).includeStats(true) etc.)


## Transactions

Right now everything runs through the "auto commit" transactional endpoint. In a JDBC world when you want to
run a long lived transaction that has many statements, you would normally disable this so you can chain statements together in one atomic execution.
In the Neo4J client you don't need to do that. I call this an Atomic Transaction and pretty much every use case can be used through it.
It has the added upside that your statements either all commit or not at all. Very ACiD and no need to handle rollbacks.

I'm currently debating whether to support "Long" Transactions, or conventional transactions.



# Installation

I'm currently trying to put this up at maven central. Check back later to see if it's up.

This driver has NO dependencies. That's right. No dependencies! Just drop it in and you're ready to go.


# Roadmap

- Clean up API to make it a bit more friendly.
- Debate whether to add or completely remove Long Transactions.
