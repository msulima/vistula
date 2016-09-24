package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.Variable


sealed trait Token

case class Observable(token: Token) extends Token

case class Box(token: Token) extends Token

case class Constant(value: String) extends Token

case class Introduce(variable: Variable, body: Token) extends Token

case class Import(variable: Variable) extends Token

case class IntroduceClass(classDef: Ast.stmt.ClassDef) extends Token

trait Operator {

  def apply(inputs: List[Constant]): String
}

case class Operation(operator: Operator, inputs: Seq[Token]) extends Token
