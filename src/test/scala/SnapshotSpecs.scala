import org.scalatest.FlatSpec
import scalax.file.Path

import edu.udlap.graph.Graph
import edu.udlap.graph.Snapshot

class SnapshotSpecs extends FlatSpec {
  val testGraphPath = "target/snapshottest"
  val graph = new Graph(testGraphPath)

  // Insert all persons
  graph.insertPerson("ID01", "Franco")
  graph.insertPerson("ID02", "Francisco")
  graph.insertPerson("ID03", "Ernesto")
  graph.insertPerson("ID04", "Esteban")
  graph.insertPerson("ID05", "Ofelia")
  graph.insertPerson("ID06", "Alfredo")
  graph.insertPerson("ID07", "Juan")
  graph.insertPerson("ID08", "Pedro")
  graph.insertPerson("ID09", "Susana")
  graph.insertPerson("ID10", "Julia")

  // Relations of Franco
  graph.relatePersons("ID01", "ID02")
  graph.relatePersons("ID01", "ID03")
  graph.relatePersons("ID01", "ID04")
  graph.relatePersons("ID01", "ID05")

  // Relations of Ofelia
  graph.relatePersons("ID05", "ID06")
  graph.relatePersons("ID05", "ID07")
  graph.relatePersons("ID05", "ID08")

  // Relations of Susana
  graph.relatePersons("ID09", "ID04")
  graph.relatePersons("ID09", "ID06")

  // Relations of Ernesto
  graph.relatePersons("ID03", "ID10")

  val snapshot = new Snapshot(graph)

  val numberOfNodes = 10L
  val degreeOfID01 = 4f/9f
  val betweennessOfID01 = (2f*(23f))/(numberOfNodes*numberOfNodes - 3f*numberOfNodes + 2)
  val closenessOfID05 = 1.6666666f

  "Snapshot.n" should "have the correct number of nodes in the graph" in {
    assert(snapshot.n == numberOfNodes)
  }

  "Snapshot.degreeOf(u)" should "compute the right centrality value" in {
    val value = snapshot.degreeOf("ID01")
    assert(value == degreeOfID01)
  }
 
  "Snapshot.betweennessOf(u)" should "compute the right centrality value" in {
    val value = snapshot.betweennessOf("ID01")
    assert(value == betweennessOfID01)
  }

  "Snapshot.closenessOf(u)" should "compute the right centrality value" in {
    val value = snapshot.closenessOf("ID05")
    assert(value == closenessOfID05)
  }

  "TEST" should "destroy it's testing database" in {
    graph.destroy
    val testPath = Path.fromString(testGraphPath)
    assert(!testPath.exists)
  }

}
