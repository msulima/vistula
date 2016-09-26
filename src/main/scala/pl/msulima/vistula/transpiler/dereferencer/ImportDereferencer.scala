package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.Operation
import pl.msulima.vistula.transpiler.expression.data.StaticDict
import pl.msulima.vistula.transpiler.expression.reference.Declare
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopedResult}
import pl.msulima.vistula.{Package, Vistula}

object ImportDereferencer {

  private val EmptyDict = Operation(StaticDict, Seq())

  def packagePreambule(`package`: Package) = {
    if (`package` == Package.Root) {
      Seq()
    } else {
      val root = Declare(`package`.path.head, EmptyDict, mutable = false, declare = true)
      val modules = `package`.path.inits.toSeq.reverse.drop(2).map(path => {
        Declare(Package(path).toIdentifier, EmptyDict, mutable = false, declare = false)
      })
      root +: modules
    }
  }
}

trait ImportDereferencer {
  this: Dereferencer =>

  def importDereferencer(`import`: Ast.stmt.Import) = `import` match {
    case classDef@Ast.stmt.Import(Ast.alias(identifier, None) +: _) =>
      val declarations = Vistula.loadFile(identifier).declarations

      val scopePart = declarations.copy(classes = declarations.classes.map({
        case (id, definition) =>
          ClassReference(identifier.name) -> definition
      }))

      val ns = scopePart.classes.toSeq.foldLeft(scope)({
        case (acc, (id, definition)) => acc.addToScope(id, definition)
      })

      ScopedResult(ns, Seq())
  }
}
