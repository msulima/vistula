package pl.msulima.vistula

import java.nio.file.{Files, Path}

import fastparse.all._
import pl.msulima.vistula.parser.Statements
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}

import scala.collection.JavaConversions._

object Vistula {

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input))
  }

  def loadFile(file: Path) = {
    Transformer.extractScope(parse(Files.readAllLines(file).mkString("\n")))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
