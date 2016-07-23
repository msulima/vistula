package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class Type(/*name: String,*/ observable: Boolean)

case class Variable(id: Ast.identifier, `type`: Type)

case class ScopedResult(scope: Scope, program: Seq[Token])

case class Scope(variables: Seq[Variable]) {

  def addToScope(variable: Variable) = {
    copy(variables :+ variable)
  }
}
