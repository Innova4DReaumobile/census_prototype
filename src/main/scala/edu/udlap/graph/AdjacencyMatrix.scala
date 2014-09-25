package edu.udlap.graph
import scala.util.Random

/** An Adjacency Matrix to represent Graphs
* @constructor Creates a Matrix with a defined number of nodes.
* @param numberOfNodes : The number of nodes in the graph  (Must be positive Int).
* @param age the person's age in years 
**/
class AdjacencyMatrix (val numberOfNodes: Int){
    
    if(numberOfNodes < 0)
        throw new IllegalArgumentException("Number of Nodes must be Non-negative.")

    var matrix = Array.ofDim[Int](numberOfNodes,numberOfNodes)    

    /** 
    * Generates a Simple Undirected Graph
    * The global property "matrix" is filled with the new random graph generated.
    **/

    def  generateSimpleRandomUndirectedGraphWith() =  {
        for(i <- 0 until matrix.length){
            for(j <- 0 until matrix(i).length){
                if(i == j) 
                    matrix(i)(j) = 0 //No loops - Simple Graph...
                else if(j > i) 
                    matrix(i)(j) = Random.nextBoolean.compare(false)
                    matrix(j)(i) = matrix(i)(j)
            }
        }
    }    

    /** 
    * Generates a Simple Undirected Graph with relationship probabilities.
    * @param probabilityOfRelationship : the probability that a relationship is created
    * The global property "matrix" is filled with the new random graph generated.
    **/

    def  generateSimpleRandomUndirectedGraphWith(probabilityOfRelationship: Double) = {
        for(i <- 0 until matrix.length){
            for(j <- 0 until matrix(i).length){
                if(i == j) 
                    matrix(i)(j) = 0 //No loops - Simple Graph...
                else if(j > i) {
                    var randomNumber : Double = Random.nextDouble()
                    matrix(i)(j) = (((0 + randomNumber * (1 - 0))> probabilityOfRelationship).compare(true))*(-1)
                    matrix(j)(i) = matrix(i)(j)
                }
            }
        }
    }
    /** 
    * Generates a Simple Scale-Free undirected graph using BA Model.
    * The global property "matrix" is filled with the new random graph generated.
    **/
    def generateSimpleScaleFreeUndirectedGraph() = {
        matrix(0)(1) = 1 
        matrix(1)(0) = 1 //A scale-free graph starts at least with 1 relationship between nodes.
        for(i <- 2 until matrix.length){
            var j = 0
            var degreeCentralityNodes = this.calculateDegreeCentralitiesForAllNodes()
            var sum = this.summatoryFromArray(degreeCentralityNodes)
            //var ensureRelationshipCounter : Int = 0
            while(i!=j){
                if(j < i){
                    if(degreeCentralityNodes(j)!=0) {
                        var currentNode: Double = degreeCentralityNodes(j).toDouble
                        var probability: Double = currentNode/(sum.toDouble/2.0-currentNode)
                        var randomNumber : Double = Random.nextFloat()
                        matrix(i)(j) = (((0 + randomNumber * (1 - 0)) > probability).compare(true))*(-1)
                        matrix(j)(i) = matrix(i)(j)
                        //ensureRelationshipCounter += matrix(i)(j)
                    }
                }
                //Ensuring relationships adds complexity and makes many bugs, maybe we can resolve that later...
                //if(ensureRelationshipCounter !=0) 
                j += 1
            }
            if(i==j) 
                matrix(i)(j) = 0
        }
    }

    def calculateDegreeCentralitiesForAllNodes() : Array[Int] = {
    var degreeCentralityNodes = Array.ofDim[Int](numberOfNodes)
        for(i <- 0 until matrix.length){
            for(j <- 0 until matrix(i).length){
                degreeCentralityNodes (i) += matrix(i)(j) 
            }
        }
        return degreeCentralityNodes
    }

    def summatoryFromArray(Array: Array[Int]) : Int = {
        var result: Int = 0;
        for(i <- 0 until Array.length){
            result += Array(i)
        }
        return result
    }
    /** 
    * Override of the toString method.
    * Strings the content of the matrix property.
    **/
    override def toString(): String = {
        var str : String = ""
        for(i <- 0 until matrix.length){
            for(j <- 0 until matrix(i).length)
                str += matrix(i)(j)+" "
            str += "\n"
        }
        return str
     }
}
