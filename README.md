Census v0.1.1
=============
A library that calculates useful social metrics from a social graph, using Neo4j and written in Scala.

Features
--------
* Compatible with the JVM.
* Degree centrality metric calculation.
* Betweenness centrality metric calculation.
* Closeness centrality metric calculation.
* Graph snapshots for efficient global centralities calculation and records over time. 

Assembly the .jar
-----------------
To create a packaged jar just run this command in the root dorectory:

```bash
cd path/to/census
sbt assembly 
``` 

The .jar will be created in `path/to/census/target/scala-2.10/census-vX.X.jar`.

How to use
----------
_Note: Normally you will need to calculate this social metrics globaly (for all nodes), in that case you should use the Snapshot feature._

First import the api package.

```java
import edu.udlap.census.CensusAPI;
```

Then instantiate the api. The constructor recieves a string which is the path to where the database will be created.

```java
CensusAPI api = new CensusAPI("target/testdb");
```

### api.insertPerson (id: String, name: String) -> void
Inserts a person with specified id and name into the graph database.

### api.relatePersons (aid: String, bid: String) -> void
Relates two persons with specified ids.

### api.degreeOf (id: String) -> float
Calculates the relative degree centrality of a node with the specified id.

### api.betweennessOf (id: String) -> float
Calculates the betweenness centrality of a node with the specified id.

### api.closenessOf (id: String) -> float
Calculates the closeness centrality of a node with the specified id.

Graph Snapshot
--------------
A graph snapshot is a recorded state of a graph at any time, when created it calculates the 3 centralities and all shortest paths for ALL the nodes in the graph, it is useful because it is more efficient overall and you can have a record of change.

_Note: a snapshot calculates the **relative** betweenness and degree of the nodes, which should be a much more useful measure._

### How to use the Snapshot Feature

First import the Snapshot and Graph classes.

```java
import edu.udlap.graph.Graph;
import edu.udlap.graph.Snapshot;
```

Then create a graph.

```java
Graph graph = new Graph("path/to/the/graph/db");
```

After that add some persons to the graph and relate them.

```java
graph.insertPerson("ID01", "Franco");
graph.insertPerson("ID02", "Francisco");
graph.relatePersons("ID01", "ID02");
```

Then create a snapshot with the graph.

```java
Snapshot snapshot = new Snapshot(graph);
```

Depending on how many nodes the calculation of the 3 centralities on ALL nodes could take some time; after that time is finished you can access to the data through the centralities caches or though the centrality methods.

```java
graph.degreeOf("ID01");
graph.betweennessOf("ID01");
graph.closenessOf("ID01");
```

### Attributes of a Snapshot object

#### n
The number of nodes in the graph.

#### ids
A List of all the ids in the graph.

#### timestamp
A `System.currentTimeMillis` record of the snapshot.

#### shortestPaths
A `Map <String, List <List <String>>>` with all the shortest paths between all nodes. They key is in the form `<id1>-<id2>`. 

#### degreeCache
A `Map <String, Float>` with all the degree centralities. The key is in the form `<node-id>`, for example: 

```java
float francosDegree = snapshot.degreeCache.get("ID01");
```

#### betweennessCache
A `Map <String, Float>` with all the betweenness centralities.

#### closenessCache
A `Map <String, Float>` with all the closeness centralities.

### Methods of a Snapshot object

#### snapshot.degreeOf (id: String) -> float
Retrieves the relative degree centrality of a node with the specified id.

#### snapshot.betweennessOf (id: String) -> float
Retrieves the relative betweenness centrality of a node with the specified id.

#### snapshot.closenessOf (id: String) -> float
Retrieves the closeness centrality of a node with the specified id.

TODO
----
* Parallelize the shortest paths computation for the snapshot.

License
-------
The MIT License (MIT)

Copyright (c) 2013 Francisco Miguel Arámburo Torres

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

