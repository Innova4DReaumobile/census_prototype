package edu.udlap.census

import edu.udlap.graph.Graph
import edu.udlap.analysis._

class CensusAPI (dbPath: String) {
  val graph = new Graph(dbPath)

  def insertPerson (id: String, name: String) = graph.insertPerson(id, name)
  def relatePersons (aid: String, bid: String) = graph.relatePersons(aid, bid)
  def degreeOf (id: String) = Degree.of(graph, id)
  def betweennessOf (id: String) = Betweenness.of(graph, id)
  def closenessOf (id: String) = Closeness.of(graph, id)
}
