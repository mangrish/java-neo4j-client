java-neo4j-client
=================

A compact Neo4J Java API for connecting to non embedded instances.

# Features

1. No 3rd party dependencies (except [SLF4J](http://www.slf4j.org/)).
1. Very simple, Neo4J specific API that only uses Cypher, the Neo4J query language.
1. Support for Graph and Row based result retrieval.
1. Allows multiple queries per Transaction. Useful for batching or realising patterns like [Unit of Work](http://martinfowler.com/eaaCatalog/unitOfWork.html).
1. Built to be used with the [Java Neo4J OGM](https://github.com/inner-loop/java-neo4j-ogm).
1. Can be used as a placeholder API until the official Neo4J Binary Protocol Java driver is released.

# Usage

## Install

This driver currently requires Java 8+.

To install from Maven:

```maven
<dependency>
    <groupId>io.innerloop</groupId>
    <artifactId>java-neo4j-client</artifactId>
    <version>0.2.0</version>
</dependency>
```

To install from Gradle:

```gradle
compile group: 'io.innerloop', name: 'java-neo4j-client', version: '0.2.0'
```

## Initialise

Initialise the driver like so:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");
```

or with credentials:

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data", "username", "password");
```

You will only need one of these instances per application. This object is thread safe so feel free to share it.

## Connections and Statements.

java-neo4j-driver Connections are a little like JDBC Connections and Transactions merged together.

All Neo4J Queries must run within a Neo4J Transaction.  This driver allows batches of statements to be 
flushed to the database intermittently before being committed. It also provides the capability to rollback Transactions
on a connection.

Statements also come in two flavours:

1. _Graph Statements_: Will return results in a graph format. Useful when you want to visualise your graph or map it to
domain objects etc. It is important to remember that when performing Graph queries you will need to return relationships, not just the nodes!  
For example: ```MATCH (n:Node)-[r]-() RETURN n, COLLECT(r) AS r```
1. _Row based Statements_: Will return results in a more JDBC familiar table format, with column names and rows of data.
Useful if you want to get aggregate results or perform more relational type queries. This mode is useful when you want
to extract tabular data from your Neo4J server. This method will return items back as RowSets. RowSets are just stripped
 down versions of the JDBC ResultSet. You can iterate through RowSets using the next() method.

Statements also support parameter replacement. Simply use a placeholder in your cypher query like so: ```{ placeholder }```. See 
examples for more details.


## Examples

### Basic Example.

This example will use two existing items (A User and a Tweet) and connect them together. It will then
retrieve all tweets tweeted by a user then flush them to the database, thus allowing us to see the results of 
the call.

We then do an update the user object itself before committing the result and getting the neo4j id of the user.

It is worth noticing that when ```flush()``` is called, the transaction is still alive. It's important to not too
too much data manipulation inside of the transactions otherwise it could time out.  If you anticipate a very long
running transaction you may call the Connection.resetExpiry() method.

```java
Neo4jClient client = new Neo4jClient("http://localhost:7474/db/data");

Connection connection = client.getConnection(); // gets the active connection on this Thread.

RowStatement statement1 = new RowStatement("MATCH (a:User{id:{0}}), (b:Tweet{id:{1}}) MERGE (a)-[:TWEETED]-(b)");
statement1.setParam("0", "a1b2c3d4");
statement1.setParam("1", "e5f6g7h8");
connection.add(statement1);

GraphStatement statement2 = new GraphStatement("MATCH (a:User{id:{userId}})-[r:TWEETED]-() RETURN a, COLLECT(r) AS r");
statement2.setParam("userId", "a1b2c3d4");
connection.add(statement2);

// this will execute any statements that have already appeared. Writes are isolated 
// to this Transaction as "Read Committed" Isolation.
connection.flush(); 

Graph userTweets = statement2.getResult();

// Do something with the user tweets..
// Set<Node> nodes = userTweets.getNodes();
// Set<Relationship> relationships = userTweets.getRelationships();


RowStatement statement3 = new RowStatement("MATCH (a:User{id:{userId}}) SET a = {user} RETURN id(a)");
statement3.setParam("userId", "a1b2c3d4");
Map<String, Object> userProperties = new HashMap<>();
userProperties.put("id", "a1b2c3d4");
userProperties.put("email", "hello@kitty.com");
userProperties.put("name", "Hello Kitty);
statement3.setParam("user", new JSONObject(userProperties));
connection.add(statement3);

// Finally commit the whole thing to the database. and check out the user id.
connection.commit();

long userNeo4jId = statement3.getResult().getLong(0);
```

# Why another Driver?

There are a few Java Neo4J Drivers out there, the two most used being the neo4j-rest-binding, a relic from the Neo4J 1.x
days and the much newer neo4j-jdbc-driver. Neo4j are also working on a binary protocol with a corresponding Java driver.
This project is meant to be the bridge between that newer driver and what is already available. I hope it's API and
call semantics are similar to what the binary/native Java driver API and call semantics will look like.

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

This driver is specifically intended to work with the [Java Neo4J OGM](https://github.com/inner-loop/java-neo4j-ogm).


# Roadmap

##0.3.x
- Add performance tests.
