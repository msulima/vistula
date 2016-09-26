package pl.msulima.vistula

import java.io.{BufferedOutputStream, FileOutputStream, PrintStream}
import java.nio.file.{Files, Path}

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.dereferencer.ImportDereferencer
import pl.msulima.vistula.transpiler.scope.Scope
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}
import pl.msulima.vistula.util.Paths

import scala.collection.JavaConversions._

object Vistula {

  def compileAll() = {
    Paths.findAllSourceFiles().foreach({
      case (file, pack) =>
        val script = Transpiler.scoped(read(file), pack)

        val resolve = Paths.toTargetFile(file)

        resolve.getParent.toFile.mkdirs()
        Files.write(resolve, script.split("\n").toSeq)
    })
  }

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input), Package.Root)
  }

  def browserify(input: Package): Unit = {
    val resolve = Paths.toTargetFile(input)
    resolve.getParent.toFile.mkdirs()
    resolve.toFile.delete()

    val outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(resolve.toFile)))

    outputStream.println(Transpiler.toJavaScript(Transformer.transform(ImportDereferencer.packagePreambule(input), Scope.Empty, input)));

    Paths.findPackageSourceFiles(input).foreach({
      case (file, pack) =>
        val script = Transpiler.scoped(read(file), pack)

        script.split("\\n").foreach(outputStream.println)
    })
    outputStream.close()
  }

  def loadFile(id: Ast.identifier) = {
    Transformer.extractScope(read(Paths.findSourceFile(id)), Package(id.name))
  }

  def read(file: Path): Seq[Ast.stmt] = {
    parse(Files.readAllLines(file).mkString("\n"))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
