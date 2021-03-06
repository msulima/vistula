package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}


sealed trait Token

case class Observable(token: Token) extends Token

case class Box(token: Token) extends Token

case class IdConstant(value: Ast.identifier) extends Token

case class TypedConstant(value: String, `type`: ScopeElement) extends Token

case class ImportVariable(variable: Variable) extends Token

case class Direct(stmt: Ast.stmt) extends Token

case class Constant(value: String)

trait Operator {

  def apply(inputs: List[Constant]): String
}

case class Operation(operator: Operator, inputs: Seq[Token]) extends Token

object IdConstant {

  def expr(value: Ast.identifier): Expression = {
    ExpressionConstant(value.name, ScopeElement.DefaultConst)
  }
}
