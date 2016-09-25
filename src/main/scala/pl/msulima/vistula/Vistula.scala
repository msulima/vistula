package pl.msulima.vistula

import java.nio.file.{Files, Path}

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}
import pl.msulima.vistula.util.Paths

import scala.collection.JavaConversions._

object Vistula {

  def compileAll() = {
    Paths.findAllSourceFiles().foreach({
      case (file, path) =>
        val script = Transpiler.scoped(read(file))

        val resolve = Paths.toTargetFile(file)

        resolve.getParent.toFile.mkdirs()
        Files.write(resolve, script.split("\n").toSeq)
    })
  }

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input))
  }

  def loadFile(id: Ast.identifier) = {
    Transformer.extractScope(read(Paths.findSourceFile(id)))
  }

  def read(file: Path): Seq[Ast.stmt] = {
    parse(Files.readAllLines(file).mkString("\n"))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
