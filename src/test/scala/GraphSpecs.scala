import org.scalatest.FlatSpec
import scalax.file.Path

import edu.udlap.graph.Graph
import edu.udlap.graph.AdjacencyMatrix

/** TESTS TODO:
 *
 *  def allIdsBut (u)
 *  def geodesicsBetween (i, j)
 *  def generateNewRandomSocialGraphFrom (adjacencyMatrix, nodeProperties)
 *  def addRandomSocialGraphToCurrentGraphFrom (adjacencyMatrix, nodeProperties)
 *  def clear ()
 *  def clearAllNodesAndRelationshipsButRoot ()
 *  def isDataBaseAvailable ()
 *  def isDataBaseEmpty ()
 */

class GraphSpecs extends FlatSpec {
  val testGraphPath = "target/testdb"
  val graph = new Graph(testGraphPath)
  val numberOfNodes = 10
  val matrix = new AdjacencyMatrix(numberOfNodes)
  var propertiesMatrix = Array.ofDim[String](numberOfNodes, 0)

  "def query()" should "return the successful ExecutionResult on valid cypher query" in {
    val result = graph.query("START n=node(*) RETURN n")
    assert(result.getClass.getName == "org.neo4j.cypher.javacompat.ExecutionResult")
  }

  "def insertPerson(id, name)" should "create a person node in the database" in {
    graph.insertPerson("ID01", "Franco")
    val result = graph.query(
      "START n=node(*)"+
      "WHERE HAS (n.id) AND n.id='ID01'"+
      "RETURN COUNT(n) AS count")
    val res = result.columnAs("count")
    val one:java.lang.Long = 1L
    val count:java.lang.Long = res.next
    assert(count == one)
  }

  it should "create unique person nodes" in {
    graph.insertPerson("ID02", "Francisco")
    graph.insertPerson("ID02", "Francisco")
    val result = graph.query(
      "START n=node(*)"+
      "WHERE HAS (n.id) AND n.id='ID02'"+
      "RETURN COUNT(n) AS count")
    val res = result.columnAs("count")
    val one:java.lang.Long = 1L
    val count:java.lang.Long = res.next
    assert(count == one)
  }

  "def relatePersons(aid, bid)" should "create a relationship of type KNOWS between nodes" in {
    graph.relatePersons("ID01", "ID02")
    val result = graph.query(
      "START a=node:id(id='ID01'), b=node:id(id='ID02') "+
      "MATCH (a)-[r:KNOWS]-(b)"+
      "RETURN COUNT(r) as count")
    val res = result.columnAs("count")
    val one:java.lang.Long = 1L
    val count:java.lang.Long = res.next
    assert(count == one)
  }

  it should "create unique KNOWS relationships" in {
    graph.relatePersons("ID02", "ID01")
    graph.relatePersons("ID02", "ID01")
    val result = graph.query(
      "START a=node:id(id='ID01'), b=node:id(id='ID02') "+
      "MATCH (b)-[r:KNOWS]-(a)"+
      "RETURN COUNT(r) as count")
    val res = result.columnAs("count")
    val one:java.lang.Long = 1L
    val count:java.lang.Long = res.next
    assert(count == one)
  }

  "def clear" should "return a new empty instance of database" in{
    graph.clear()
    assert(graph.isDataBaseEmpty())
  }

  "def importGraphFrom(adjacencyMatrix, nodeProperties)" should "insert nodes equal to adjacencymatrix" in {
    matrix.generateSimpleScaleFreeUndirectedGraph()
    graph.importGraphFrom(matrix,propertiesMatrix)
    assert(graph.countNodes == numberOfNodes)
  }

  it should "insert relationships as represented in adjacencymatrix" in {
    var degreeCentralityNodes = matrix.calculateDegreeCentralitiesForAllNodes()
    var sum = matrix.summatoryFromArray(degreeCentralityNodes)
    assert(graph.countRelationships == sum/2)
  }

  it should "insert properties in nodes as represented in propertiesMatrix" in {
    //names from resources list.
  }

  "def destroy()" should "destroy the database files" in {
    graph.destroy
    val testPath = Path.fromString(testGraphPath)
    assert(!testPath.exists)
  }
}
