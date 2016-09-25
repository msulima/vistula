package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.Vistula
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopedResult}

trait ImportDereferencer {
  this: Dereferencer =>

  def importDereferencer(`import`: Ast.stmt.Import) = `import` match {
    case classDef@Ast.stmt.Import(Ast.alias(identifier, None) +: _) =>
      val imported = identifier.name.split("\\.")
      val prefix = imported.init
      val path = (Seq("main", "vistula") ++ prefix) :+ (imported.last + ".vst")

      val declarations = Vistula.loadFile(identifier).declarations

      val scopePart = declarations.copy(classes = declarations.classes.map({
        case (id, definition) =>
          ClassReference((prefix :+ id.name).map(Ast.identifier)) -> definition
      }))

      val ns = scopePart.classes.toSeq.foldLeft(scope)({
        case (acc, (id, definition)) => acc.addToScope(id, definition)
      })

      ScopedResult(ns, Seq())
  }
}
