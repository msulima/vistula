package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class ScopedResult(code: String, scope: Scope)

case class Scope(variables: Seq[Ast.identifier], observables: Seq[Ast.identifier]) {

  def apply(code: String) = ScopedResult(code, this)
}
