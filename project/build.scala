import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object HintBuild extends Build {
  val Name = "census"
  val Organization = "edu.udlap"
  val Version = "0.1.1"
  val ScalaVersion = "2.10.3"

  lazy val hintProject: Project = Project(
    "census", 
    file("."),
    settings = Project.defaultSettings ++ assemblySettings ++ Seq (
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "2.0" % "test",
        "org.neo4j" % "neo4j" % "1.9.4",
        "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.2",
        "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"
      ),
      jarName in assembly := Name+"-v"+Version+".jar", 
      test in assembly := {},
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => 
        {
          case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
          case "META-INF/CHANGES.txt" => MergeStrategy.first
          case "META-INF/LICENSES.txt" => MergeStrategy.first
          case x => old(x)
        }
      } // mergeStrategy
    ) // settings
  ) // Project

}
