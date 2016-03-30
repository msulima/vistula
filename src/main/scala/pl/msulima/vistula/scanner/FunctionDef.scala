package pl.msulima.vistula.scanner

import pl.msulima.vistula.Ast
import pl.msulima.vistula.Ast.stmt

object FunctionDef {

  val apply: PartialFunction[stmt, Set[String]] = {
    case stmt.FunctionDef(name: Ast.identifier, args: Ast.arguments, body: Seq[stmt], _) =>
      val argumentNames = args.args.map(arg => arg match {
        case Ast.expr.Name(id, _) =>
          id.name
      }).toSet

      argumentNames ++ body.flatMap(Statement.apply)
  }
}
