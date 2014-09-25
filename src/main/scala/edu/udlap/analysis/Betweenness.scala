package edu.udlap.analysis

import java.util.{List => JavaList}
import edu.udlap.graph.Graph

/** Module that computes the bewtweenness centrality */
object Betweenness {

  /** Calculates betweenness of node with id 'u'
   *
   * @param graph to be used for the calculation.
   * @param u id of node to be used.
   * @return the centrality value of node with id 'u'
   */
  def of (graph: Graph, u: String): Float = {
    val ids: JavaList[String] = graph.allIdsBut(u)
    var sum: Float = 0.0f

    // iterates through all node pairs in the graph and sums 
    // the potential control of 'u'
    for (i <- 0 until ids.size) {
      for (j <- i+1 until ids.size) {
        sum += potentialControl(graph, ids.get(i), u, ids.get(j))
      }
    }

    sum
  }
  
  /** Calculates teh potential control of node 'u' between nodes 'i' and 'j'
   *
   * @param graph to be used for the calculation
   * @param i node
   * @param u node
   * @param j node
   * @return potential control value of u
   */
  private def potentialControl (graph: Graph, i: String, u: String, j: String): Float = {
    // geodesics between i and j
    val geos: JavaList[JavaList[String]] = graph.geodesicsBetween(i, j)
    // one specific geodesic
    var geo: JavaList[String] = null
    // total number of geodesics between i and j
    val numGeodesics = geos.size
    // number of geodesics that contains u
    var numContains = 0

    for (k <- 0 until numGeodesics) {
      // one geodesic
      geo = geos.get(k)
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

}
