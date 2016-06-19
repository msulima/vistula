package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, RxMap, Scope, Static}

class Name(scope: Scope) {

  def apply: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      if (scope.variables.contains(id)) {
        CodeTemplate(id.name, mapper = Static)
      } else if (!scope.mutable) {
        CodeTemplate(s"${id.name}.rxLastValue()", mapper = Static)
      } else {
        CodeTemplate(id.name, mapper = RxMap)
      }
  }
}
