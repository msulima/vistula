package pl.msulima.vistula

import java.nio.file.{Files, Path, StandardOpenOption}

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}
import pl.msulima.vistula.util.Paths

import scala.collection.JavaConversions._

object Vistula {

  def compileAll() = {
    Paths.findAllSourceFiles().foreach({
      case (file, _) =>
        val script = Transpiler.scoped(read(file))

        val resolve = Paths.toTargetFile(file)

        resolve.getParent.toFile.mkdirs()
        Files.write(resolve, script.split("\n").toSeq)
    })
  }

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input))
  }

  def browserify(input: Package): Unit = {
    val resolve = Paths.toTargetFile(input)
    resolve.getParent.toFile.mkdirs()
    resolve.toFile.delete()

    Paths.findPackageSourceFiles(input).foreach({
      case (file, _) =>
        val script = Transpiler.scoped(read(file))

        Files.write(resolve, script.split("\n").toSeq, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
    })
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
