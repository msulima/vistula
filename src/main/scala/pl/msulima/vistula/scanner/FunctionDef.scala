package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object FunctionDef {

  def apply2: PartialFunction[Ast.stmt, Seq[Variable]] = {
    case stmt.FunctionDef(name, _, _, _) =>
      Seq.empty
    case Ast.stmt.Return(value) =>
      Seq.empty // value.map(Expression.parseExpression).toSet.flatten
  }
}
