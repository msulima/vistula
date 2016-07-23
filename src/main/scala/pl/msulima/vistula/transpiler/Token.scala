package pl.msulima.vistula.transpiler

import pl.msulima.vistula.util.Indent


sealed trait Token {

  def prettyPrint: String = toString
}

case class Observable(token: Token) extends Token {

  override def prettyPrint: String = {
    s"""Observable
        |${Indent.leftPad(token.prettyPrint)}
     """.stripMargin
  }
}

case class Box(token: Token) extends Token

case class Constant(value: String) extends Token

trait Operator {

  def apply(inputs: List[Constant], output: Constant): Constant
}

case class Operation(operator: Operator, inputs: Seq[Token], output: Token) extends Token {

  override def prettyPrint: String = {
    val inps = if (inputs.isEmpty) {
      Seq("<no input>")
    } else {
      inputs.map(token => s"--> ${token.prettyPrint}")
    }
    s"""Op: $operator
        |${Indent.leftPad(inps)}
        |${Indent.leftPad(s"<-- ${output.prettyPrint}")}""".stripMargin
  }
}
