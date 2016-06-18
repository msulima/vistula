package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Assign {

  def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    case Ast.stmt.DeclareStmt(identifier, value, mutable) =>
      if (mutable) {
        scope.copy(observables = scope.observables :+ identifier) {
          s"const ${identifier.name} = ${Transpiler(scope, value)}"
        }
      } else {
        scope.copy(variables = scope.variables :+ identifier) {
          s"const ${identifier.name} = ${static.Transpiler(scope, value)}"
        }
      }
    case Ast.stmt.AssignStmt(expr, value) =>
      scope {
        s"${Transpiler(scope, value)}.rxForEachOnce($$arg => ${Transpiler(scope, expr)}.rxPush($$arg))"
      }
  }
}
