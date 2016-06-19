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
        val transpiler = Transpiler.scoped(scope.copy(mutable = mutable), value)

        s"const ${identifier.name} = ${transpiler.asCodeObservable}"
      }
    case Ast.stmt.AssignStmt(expr, value) =>
      scope {
        s"${Transpiler.scoped(scope, value).asCodeObservable}.rxForEachOnce($$arg => ${Transpiler.scoped(scope, expr).asCodeObservable}.rxPush($$arg))"
      }
  }
}
