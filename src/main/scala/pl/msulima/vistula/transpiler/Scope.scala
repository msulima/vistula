package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class ScopedResult(scope: Scope, result: Result) {

  def asCodeObservable = {
    if (scope.mutable && !result.mutable) {
      s"vistula.constantObservable(${result.code})"
    } else {
      s"${result.code}"
    }
  }
}

case class Scope(variables: Seq[Ast.identifier], observables: Seq[Ast.identifier], mutable: Boolean) {

  def apply(code: String) = ScopedResult(this, Result(code, mutable = true))

  def apply(result: Result) = ScopedResult(this, result)

}
