package pl.msulima.vistula

import java.nio.file.{Files, Path}

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}
import pl.msulima.vistula.util.Paths

import scala.collection.JavaConversions._

object Vistula {

  def compileAll() = {
    val sourceDir = java.nio.file.Paths.get("src", "main", "vistula")
    val targetDir = java.nio.file.Paths.get("target", "vistula", "classes")

    Paths.deleteRecursively(targetDir.toFile)

    Paths.getAllFiles(sourceDir).foreach({
      case (file, path) =>
        val script = Transpiler.scoped(read(file))

        val subpath = file.subpath(sourceDir.getNameCount, file.getNameCount - 1)
        val resolve = targetDir.resolve(subpath).resolve(file.getFileName.toString.replaceAll("\\.vst$", ".js"))

        resolve.getParent.toFile.mkdirs()
        Files.write(resolve, script.split("\n").toSeq)
    })
  }

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input))
  }

  def loadFile(file: Path) = {
    Transformer.extractScope(read(file))
  }

  def read(file: Path): Seq[Ast.stmt] = {
    parse(Files.readAllLines(file).mkString("\n"))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
