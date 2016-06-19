package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Assign {

  def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    case Ast.stmt.DeclareStmt(identifier, value, mutable) =>
      val nextScope = if (mutable) {
        scope.copy(observables = scope.observables :+ identifier, mutable = mutable)
      } else {
        scope.copy(variables = scope.variables :+ identifier, mutable = mutable)
      }

      nextScope {
        s"const ${identifier.name} = ${Transpiler(scope.copy(mutable = mutable), value)}"
      }
    case Ast.stmt.AssignStmt(expr, value) =>
      scope {
        s"${Transpiler(scope, value)}.rxForEachOnce($$arg => ${Transpiler(scope, expr)}.rxPush($$arg))"
      }
  }
}
