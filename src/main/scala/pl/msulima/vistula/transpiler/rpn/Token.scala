package pl.msulima.vistula.transpiler.rpn


sealed trait Token

case class Observable(token: Token) extends Token

case class Box(token: Token) extends Token

case class Constant(value: String) extends Token

trait Operator {

  def apply(inputs: List[Constant], output: Constant): Constant
}

case class Operation(operator: Operator, inputs: Seq[Token], output: Token) extends Token
