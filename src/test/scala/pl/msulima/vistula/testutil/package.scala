package pl.msulima.vistula

import java.io.{File, FileNotFoundException}
import java.nio.file.Files

import fastparse.all._
import pl.msulima.vistula.parser.Statements

import scala.collection.JavaConversions._
import scala.io.Source


package object testutil {

  def readFile(file: String): String = {
    tryReadFromClasspath(file).orElse(tryReadFromCurrentDir(file)).getOrElse(
      throw new FileNotFoundException(s"$file not found")
    )
  }

  private def tryReadFromClasspath(file: String): Option[String] = {
    Option(getClass.getResourceAsStream(file)).map(inputStream => {

      val s = Source.fromInputStream(inputStream)
      val content = s.getLines().mkString("\n")
      s.close()
      content
    })
  }

  private def tryReadFromCurrentDir(path: String): Option[String] = {
    val file = new File(path)
    if (file.exists()) {
      Some(Files.readAllLines(file.toPath).mkString("\n"))
    } else {
      None
    }
  }

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }
  }

}
