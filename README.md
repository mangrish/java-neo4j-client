java-neo4j-client
=================

A compact Java driver that supports Graphs natively for standalone Neo4J instances.

# Background

There are a few Java Neo4J Drivers out there, the two most used being the neo4j-rest-binding, a relic from the Neo4J 1.x
days and the much news neo4j-jdbc-driver. The jdbc driver suffers from some major problems for some use cases:

1. It shoehorns the power of Neo4J's unique graph ability into a relational databases model. Compromising some of the
pure power you get from graph querying.
1. The Neo4J jdbc driver does also not support "graph" mode. Graph mode massively reducse the response payload and
it allows users to store and retrieve object graphs, natively.
1. It's not lightweight. I have to add a whole heap of dependencies just to make it work.

I wanted something that allows me to query Neo4J in form that makes it so powerful: the graph; but still have the ability
to fall back on row querying when i needed it. This of this driver as the Java equivalent of of the Neo4j Web Browser and
also has no other dependencies. Just like a driver should!


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

```

    Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");

    Transaction transaction = client.getAtomicTransaction();

    Statement<Graph> statement1 = new GraphStatement("MATCH (:Label{uuid:{uuid}})-[rels]-() RETURN rels")
    statement1.addParam("uuid", "abcd1234");

    Statement<RowSet> statement2 = new RowStatement("MATCH (n:Label) RETURN count(n)")

    transaction.add(statement1);
    transaction.add(statement2);

    transaction.commit();

    Graph graph = statement1.getResult(); // Do your Graph stuff here!

    RowSet rowSet = statement2.getResult();
    int count = rowSet.getInt(0); // you get the idea!

```

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
