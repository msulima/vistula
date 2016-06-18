package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Assign {

  def apply(scope: Scope): PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.DeclareStmt(identifier, value, mutable) =>
      if (mutable) {
        scope.copy(observables = scope.observables :+ identifier)
        s"const ${identifier.name} = ${Transpiler(scope, value)}"
      } else {
        scope.copy(variables = scope.variables :+ identifier)
        s"const ${identifier.name} = ${static.Transpiler(scope, value)}"
      }
    case Ast.stmt.AssignStmt(stmt, value) =>
      s"${Transpiler(scope, value)}.rxForEachOnce($$arg => ${Transpiler(scope, stmt)}.rxPush($$arg))"
  }
}
