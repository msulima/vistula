package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Attribute {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Attribute(expr, identifier, Ast.expr_context.Load) =>
      Fragment(Seq(expr), useFlatMap = true) {
        case _ =>
          s"$$arg.${identifier.name}"
      }
  }
}
