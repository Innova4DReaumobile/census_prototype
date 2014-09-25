import org.scalatest.FlatSpec
import scalax.file.Path

import edu.udlap.graph.Graph
import edu.udlap.analysis._

class SNASpecs extends FlatSpec {
  val testGraphPath = "target/SNASpecsDB"
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

  val degreeOfID01 = 4.0f/9.0f
  val betweennessOfID01 = 23.0f
  val closenessOfID05 = 1.6666666f

  "Degree.of(graph, u)" should "compute the right centrality value" in {
    val value = Degree.of(graph, "ID01")
    assert(value == degreeOfID01)
  }
 
  "Betweenness.of(graph, u)" should "compute the right centrality value" in {
    val value = Betweenness.of(graph, "ID01")
    assert(value == betweennessOfID01)
  }

  "Closeness.of(graph, u)" should "compute the right centrality value" in {
    val value = Closeness.of(graph, "ID05")
    assert(value == closenessOfID05)
  }

  "TEST" should "destroy it's testing database" in {
    graph.destroy
    val testPath = Path.fromString(testGraphPath)
    assert(!testPath.exists)
  }

}
