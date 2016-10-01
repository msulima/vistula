package pl.msulima.vistula

import java.io.{BufferedOutputStream, FileOutputStream, PrintStream}
import java.nio.file.{Files, Path}

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.dereferencer.ImportDereferencer
import pl.msulima.vistula.transpiler.scope.{ClassDefinitionHelper, ClassReference, Scope}
import pl.msulima.vistula.transpiler.{Transformer, Transpiler}
import pl.msulima.vistula.util.Paths

import scala.collection.JavaConversions._

object Vistula {

  private lazy val Predef = readPackageDeclarations(ClassDefinitionHelper.VistulaRoot.resolve("lang"))

  def compileAll() = {
    Paths.findAllSourceFiles().foreach({
      case (file, pack) =>
        val script = Transpiler.toJavaScript(Transformer.transform(read(file), pack, Predef))

        val resolve = Paths.toTargetFile(file)

        resolve.getParent.toFile.mkdirs()
        Files.write(resolve, script.split("\n").toSeq)
    })
  }

  def toJavaScript(input: String): String = {
    Transpiler.toJavaScript(Transformer.transform(parse(input), Package.Root, Predef))
  }

  def browserify(input: Package): Unit = {
    val resolve = Paths.toTargetFile(input)
    resolve.getParent.toFile.mkdirs()
    resolve.toFile.delete()

    val outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(resolve.toFile)))

    outputStream.println(Transpiler.toJavaScript(Transformer.transform(ImportDereferencer.packagePreambule(input), Scope.Empty, input)))

    Paths.findPackageSourceFiles(input).foreach({
      case (file, pack) =>
        val program = Transformer.transform(read(file), pack, Predef)

        val script = Transpiler.toJavaScript(program)

        outputStream.println(script)
    })
    outputStream.close()
  }

  private def readPackageDeclarations(input: Package) = {
    val imports = Paths.findPackageSourceFiles(input).map({
      case (file, pack) =>
        Transformer.extractScope(read(file), pack).declarations
    })

    imports.reduce(_.addToScope(_))
  }

  def loadFile(id: ClassReference) = {
    Transformer.extractScope(read(Paths.findSourceFile(id)), id.`package`)
  }

  private def read(file: Path): Seq[Ast.stmt] = {
    parse(Files.readAllLines(file).mkString("\n"))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
