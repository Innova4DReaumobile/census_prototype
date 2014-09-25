package edu.udlap.graph

import java.util.{List => JavaList}
import java.util.{Map => JavaMap}

import scalax.file.Path
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.index.UniqueFactory.UniqueNodeFactory
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.cypher.javacompat._
import org.neo4j.graphalgo._
import scala.collection.JavaConversions._

/** The neo4j graph wraper for the whole application usage.
 *
 * @constructor create the database from a path.
 * @param dbPath to the files of the database.
 */
class Graph (dbPath: String) {
  private var graphDB = new EmbeddedGraphDatabase(dbPath)
  private var engine =  new ExecutionEngine(graphDB)
  private var nodeFactory = createUniqueNodeFactory
  private val shutdownHook = sys.ShutdownHookThread {
    graphDB.shutdown
  }

  /** Creates a node factory which indexes through the id property to ensure
   *    node uniqueness. 
   */
  private def createUniqueNodeFactory = {
    val tx = graphDB.beginTx
    val factory = new UniqueNodeFactory(graphDB, "id") {
      override def initialize(created:Node, properties:JavaMap[String, Object]) = {
        created.setProperty("id", properties.get("id"))
      } // initialize
    } // factory
    factory
  }

  /** Destorys the database files from the dbPath. */
  def destroy = {
    graphDB.shutdown
    Path.fromString(dbPath).deleteRecursively(true)
    shutdownHook.remove
  }

  /** Uses the execution engine from the instance to make a Cypher transaction query.
   * 
   * @param q the query string.
   * @return the ExecutionResult instance.
   */
  def query (q:String):ExecutionResult = {
    val tx = graphDB.beginTx
    engine.execute(q)
  }

  /** Inserts a person node into the database.
   *
   * @param id of the inserted person.
   * @param name of the inserted person.
   */
  def insertPerson (id: String, name: String): Unit = {
    val tx = graphDB.beginTx()
    try {
      val a = nodeFactory.getOrCreate("id", id)
      a.setProperty("name", name)
      a.setProperty("type", "person")
      tx.success
    } finally {
      tx.finish
    }
  }

  /** Creates a KNOWS relationship between two existent person nodes.
   *
   * @param aId the first person id.
   * @param bId the second person id.
   */
  def relatePersons (aId: String, bId: String): Unit = {
    query(
      "START a=node:id(id='"+aId+"'), b=node:id(id='"+bId+"') "+
      "CREATE UNIQUE (a)-[:KNOWS]-(b)")
  }

  /** Gets all the ids in the graph
   *
   * @return the list with all the ids.
   */
  def allIds: JavaList[String] = {
    val q = query(
      "START n=node(*) "+
      "WHERE HAS(n.type) AND n.type='person' "+
      "RETURN COLLECT(n.id) AS ids")

    val ids = q.columnAs("ids")
    val list: JavaList[String] = ids.next
    list
  }

  /** Gets all the ids in the graph but one
   *
   * @param u the node id that won't be added to the list.
   * @return the list with all the ids.
   */
  def allIdsBut (u: String): JavaList[String] = {
    val q = query(
      "START n=node(*) "+
      "WHERE HAS(n.type) AND n.type='person' AND n.id<>'"+u+"' "+
      "RETURN COLLECT(n.id) AS ids")

    val ids = q.columnAs("ids")
    val list: JavaList[String] = ids.next
    list
  }

  /** Calculates all the geodesics between node 'i' and node 'j'
   *
   * @param i the node id.
   * @param j the node id.
   * @return a list of lists with the geodesics.
   */
  def geodesicsBetween (i: String, j: String): JavaList[JavaList[String]] = {
    val q = query(
      "START source=node:id(id='"+i+"'), destination=node:id(id='"+j+"') "+
      "MATCH p = allShortestPaths(source-[r:KNOWS*]-destination) "+
      "WHERE LENGTH(p) > 1 "+
      "WITH EXTRACT(n IN NODES(p): n.id) AS nodes "+
      "RETURN COLLECT(nodes) AS paths")

    val paths = q.columnAs("paths")
    val list: JavaList[JavaList[String]] = paths.next
    list
  }

  /** Calculates all the geodesics between node 'i' and node 'j'
   *
   * @param i the node id.
   * @param j the node id.
   * @return a list of lists with the geodesics.
   */
  def allGeodesicsBetween (i: String, j: String): JavaList[JavaList[String]] = {
    val q = query(
      "START source=node:id(id='"+i+"'), destination=node:id(id='"+j+"') "+
      "MATCH p = allShortestPaths(source-[r:KNOWS*]-destination) "+
      "WITH EXTRACT(n IN NODES(p): n.id) AS nodes "+
      "RETURN COLLECT(nodes) AS paths")

    val paths = q.columnAs("paths")
    val list: JavaList[JavaList[String]] = paths.next
    list
  }

  /** Fills the database with a new random social graph from source.
   * @param adjacencyMatrix: an adjacencymatrix object.
   * @param nodeProperties: a matrix with custom node properties. Rows: nodes. Columns: properties. 
   */
  def importGraphFrom(adjacencyMatrix: AdjacencyMatrix, nodeProperties:Array[Array[String]]) = {
    var degreeCentralityNodes = adjacencyMatrix.calculateDegreeCentralitiesForAllNodes()
    for(i <- 0 until adjacencyMatrix.numberOfNodes){
      insertPerson(""+i,"Name: "+i)
    }
    for(i <- 0 until adjacencyMatrix.numberOfNodes){
      for(j <- 0 until adjacencyMatrix.numberOfNodes){
        if(degreeCentralityNodes(i)!=0){
          if(adjacencyMatrix.matrix(i)(j)==1){
            relatePersons(i.toString,j.toString)
          }
        }
      }
    }
  } 
  def importGraphFrom(json: String) = {}
  def importGraphFrom(xml: Boolean) = {}
  def exportGraphToJson() = {}
  def exportGraphToXml() = {}
  /** 
   * Clears all from database
   */
  def clear () = {
    this.destroy
    graphDB = new EmbeddedGraphDatabase(dbPath)
    engine = new ExecutionEngine(graphDB)
    nodeFactory = createUniqueNodeFactory
    query("START r= relationship(*) DELETE r")
    query("START n= node(*) DELETE n")
  }
  /**
   * Retuns boolean if database is avaible
   */
  def isDataBaseAvailable() : Boolean = { return true }
  
  /** 
   * Retuns boolean if database is empty
   */
  def isDataBaseEmpty() : Boolean = { 
    val result = query("START n=node(*) RETURN COUNT(n)").columnAs("COUNT(n)")
    val resultCast = (result.next: scala.Long)
    return resultCast == 0
  }
  /** 
   * Retuns the number of nodes in database
   */
  def countNodes() : Long = {
    val result = query("START n=node(*) WHERE HAS(n.type) AND n.type='person' RETURN COUNT(n)").columnAs("COUNT(n)")
    val resultCast = (result.next: scala.Long)
    return resultCast
  }
  /** 
   * Retuns the number of nodes in database
   */
  def countRelationships() : Long = {
    val result = query("START r=relationship(*) RETURN COUNT(r)").columnAs("COUNT(r)")
    val resultCast = (result.next: scala.Long)
    return resultCast
  }
  /** 
  * Returns all the database nodes with their properties
  **/
  override def toString(): String = {
    val result = this.query("START source= node(*) MATCH source-[relationship]->target Return relationship").columnAs("relationship")
    var returnstring = ""
    while(result.hasNext){
      val decPath = (result.next: org.neo4j.kernel.impl.core.RelationshipProxy)
      returnstring += decPath.getStartNode.toString + " " + decPath.getEndNode.toString + "\n"
      //"[" + decPath.getProperty("id") + "][" + decPath.getProperty("name")+ "]\n" // Commented for nodes
    }
    return returnstring
  }
}
