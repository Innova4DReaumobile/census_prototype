package edu.udlap.graph

import edu.udlap.analysis.Degree
import scala.collection.mutable.{MutableList => List}
import scala.collection.mutable.Map
import java.util.{List => JavaList}

/** The Snapshot class
 *
 * @constructor create a snapshot of the graph with all the centralities.
 * @param graph to be used for the snapshot.
 */
class Snapshot (graph: Graph) {
  val n: Long = graph.countNodes 
  val ids: JavaList[String] = graph.allIds
  val timestamp: Long = System.currentTimeMillis
  var aspTime: Long = 0L 
  
  val shortestPaths: Map[String, JavaList[JavaList [String]]] = fillShortestPaths
  val degreeCache: Map[String, Float] = fillDegree
  val betweennessCache: Map[String, Float] = fillBetweenness
  val closenessCache: Map[String, Float] = fillCloseness

  def degreeOf (u: String): Float = degreeCache(u)
  def betweennessOf (u: String): Float = betweennessCache(u)
  def closenessOf (u: String): Float = closenessCache(u)

  /** Calculates all shortest paths between all nodes.
   *
   * @return a Map in this form: Map('ID01-ID02', [[ID01, ID03, ID02], [ID01, ID04, ID02]])
   */
  private def fillShortestPaths: Map[String, JavaList[JavaList [String]]] = {
    val map: Map[String, JavaList[JavaList [String]]] = Map()

    // iterates through all node pairs in the graph and sums 
    for (i <- 0 until ids.size) {
      for (j <- i+1 until ids.size) {
        // geodesics between i and j
        val iID = ids.get(i)
        val jID = ids.get(j)
        map(iID+"-"+jID) = graph.allGeodesicsBetween(iID, jID)
      }
    }
    aspTime = System.currentTimeMillis - timestamp
    map
  }

  /** Calculates the degree centrality for all the nodes.
   *
   * @return a Map in this form: Map('ID01', 1.0)
   */
  private def fillDegree: Map[String, Float] = {
    val map: Map[String, Float] = Map()
    for (i <- 0 until ids.size) {
      val iID = ids.get(i)
      map(iID) = Degree.of(graph, iID)
    }
    map
  }

  /** Calculates the betweenness centrality for all the nodes.
   *
   * @return a Map in this form: Map('ID01', 1.0)
   */
  private def fillBetweenness: Map[String, Float] = {
    val map: Map[String, Float] = Map()
    for (i <- 0 until ids.size) {
      val iID = ids.get(i)
      map(iID) = betweenness(iID)
    }
    map
  }

  /** Calculates the betweenness centrality of one node, using the shortest paths cache of
   * this object.
   *
   * @param u id of the node.
   * @return the centrality value.
   */
  private def betweenness (u: String): Float = {
    var sum: Float = 0.0f
    var restOfIds: List[String] = getAllIdsBut(u)

    // iterates through all node pairs in the graph and sums 
    // the potential control of 'u'
    for (i <- 0 until restOfIds.size) {
      for (j <- i+1 until restOfIds.size) {
        sum += potentialControl(restOfIds(i), u, restOfIds(j))
      }
    }
    val nf = n.toFloat
    val relativeBetweenness = (2f*sum)/(nf*nf - 3f*nf + 2)
    relativeBetweenness
  }

  /** Calculates teh potential control of node 'u' between nodes 'i' and 'j'
   *
   * @param i node
   * @param u node
   * @param j node
   * @return potential control value of u
   */
  private def potentialControl (i: String, u: String, j: String): Float = {
    // all geodesics between i and j
    val allgeos: JavaList[JavaList[String]] = shortestPaths(i+"-"+j)
    // geodesics between i and j with length > 2
    var geos: List[JavaList[String]] = List()

    // filter all geodesics with length > 2
    for (it <- 0 until allgeos.size) {
      if (allgeos.get(it).size > 2) {
        geos += allgeos.get(it)
      }
    }

    // total number of geodesics between i and j
    val numGeodesics = geos.size
    // number of geodesics that contains u
    var numContains = 0

    for (geo <- geos) {
      if (geo.contains(u)) {
        numContains += 1
      }
    }

    if (numGeodesics == 0 || numContains == 0) {
      0.0f
    }
    else {
      // actual potential control calculation
      numContains.toFloat/numGeodesics.toFloat
    }
  }

  /** Calculates the closeness centrality for all the nodes.
   *
   * @return a Map in this form: Map('ID01', 1.0)
   */
  private def fillCloseness: Map[String, Float] = {
    val map: Map[String, Float] = Map()
    for (i <- 0 until ids.size) {
      val iID = ids.get(i)
      map(iID) = closeness(iID)
    }
    map
  }

  /** Calculates the closeness centrality of one node, using the shortest paths cache of
   * this object.
   *
   * @param u id of the node.
   * @return the centrality value.
   */
  private def closeness (u: String): Float = {
    var sum: Float = 0f
    val allIds: List[String] = getAllIdsBut(u)
    for (j <- allIds) {
      val geos = getAllGeodesicsBetween(u, j)
      if (geos.size > 0)
        sum += geos(0).size.toFloat-1f
    }
    val closeness = sum/(n.toFloat-1f)
    closeness
  }

  /** Filters all geodesics between node 'i' and node 'j'
   * (uses the cache shortest paths of this object)
   *
   * @param i id node.
   * @param j id node.
   * @return a List in this form: List([IDO1, IDO3, ID02], [ID01, ID04, ID02])
   */
  private def getAllGeodesicsBetween (i: String, j: String): List[JavaList[String]] = {
    val list: List[JavaList[String]] = List()
    if (shortestPaths.isDefinedAt(i+"-"+j)) {
      val sp = shortestPaths(i+"-"+j)
      for (k <- 0 until sp.size) { 
        list += sp.get(k)
      }
    }
    if (shortestPaths.isDefinedAt(j+"-"+i)) {
      val sp = shortestPaths(j+"-"+i)
      for (k <- 0 until sp.size) { 
        list += sp.get(k)
      }
    }
    list
  }

  /** Gets all the ids in the graph but one (uses the cache ids of this object).
   *
   * @param u the node id that won't be added to the list.
   * @return the list with all the ids.
   */
  private def getAllIdsBut (u: String): List[String] = {
    val list: List[String] = List()
    for (i <- 0 until ids.size) {
      val iID = ids.get(i)
      if (iID != u) {
        list += iID
      }
    }
    list
  }

}
