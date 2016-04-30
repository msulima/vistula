package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Attribute {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Attribute(Ast.expr.Name(value, Ast.expr_context.Load), identifier, Ast.expr_context.Load) =>
      Fragment(
        s"""${value.name}.flatMap(function ($$arg) {
           |    return $$arg.${identifier.name};
           |})""".stripMargin)
  }
}
