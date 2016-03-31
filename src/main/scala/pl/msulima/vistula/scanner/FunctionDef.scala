package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object FunctionDef {

  val apply: PartialFunction[stmt, Set[String]] = {
    case stmt.FunctionDef(name: Ast.identifier, args: Ast.arguments, body: Seq[stmt], _) =>
      val argumentNames = args.args.map(arg => arg match {
        case Ast.expr.Name(id, _) =>
          id.name
      }).toSet

      argumentNames ++ body.flatMap(Statement.apply)
    case Ast.stmt.Return(value) =>
      Set.empty // value.map(Expression.parseExpression).toSet.flatten
  }
}
