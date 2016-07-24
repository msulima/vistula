package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class Type(/*name: String,*/ observable: Boolean)

case class Variable(id: Ast.identifier, `type`: Type)

case class ScopedResult(scope: Scope, program: Seq[Token])

case class Scope(variables: Map[Ast.identifier, Type]) {

  def addToScope(variable: Variable) = {
    copy(variables + (variable.id -> variable.`type`))
  }
}
