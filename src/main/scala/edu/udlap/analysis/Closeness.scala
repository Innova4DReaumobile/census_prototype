package edu.udlap.analysis

import java.util.{List => JavaList}
import edu.udlap.graph.Graph

/** Module that computes the closeness centrality. */ 
object Closeness {

  /** Calculates closeness of node with id 'u'
   *
   * @param graph to be used for the calculation.
   * @param u id of node to be used.
   * @return the centrality value of node with id 'u'
   */
  def of (graph: Graph, u: String): Float = {
    val q = graph.query(
      "START n=node:id(id='"+u+"'), m=node(*) "+
      "MATCH p=shortestPath(n-[KNOWS*]-m) "+
      "WHERE HAS(m.type) AND m.type='person' AND m.id<>'"+u+"'"+
      "RETURN SUM(LENGTH(RELATIONSHIPS(p))) AS sum, COUNT(m) as total")

    // FRANCISCO ARAMBURO: HAD A PROBLEM HERE, COULDN'T RETRIEVE BOTH COLUMNS 
    // SO I HAD TO USE THE graph.AllIdsBut(u) instead OF RETRIEVING THE total 
    // COLUMN OF THE QUERY.

    val qSum = q.columnAs("sum")
    val sum = (qSum.next: scala.Int).toFloat
    val ids = graph.allIdsBut(u)
    
    sum/ids.size
  }
}
