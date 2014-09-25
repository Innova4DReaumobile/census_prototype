package edu.udlap.analysis

import edu.udlap.graph.Graph

/** Module that computes the degree centrality. */ 
object Degree {

  /** Calculates degree of node with id 'u'
   *
   * @param graph to be used for the calculation.
   * @param u id of node to be used.
   * @return the centrality value of node with id 'u'
   */
  def of (graph: Graph, u: String): Float = {
    val degreeQuery = graph.query(
      "START n=node:id(id='"+u+"'), t=node(*) "+
      "MATCH (n)-[r:KNOWS]-(t) "+
      "RETURN count(r) as degree;")
    val totalQuery = graph.query(
      "START n=node(*) "+
      "WHERE HAS(n.type) AND n.type='person' "+
      "RETURN count(n) as total;")
    val degreeResult = degreeQuery.columnAs("degree")
    val totalResult = totalQuery.columnAs("total")

    val degree = (degreeResult.next: scala.Long).toFloat
    val total = (totalResult.next: scala.Long).toFloat
    
    // calculation of relative degree
    degree/(total-1.0f)
  }

}
