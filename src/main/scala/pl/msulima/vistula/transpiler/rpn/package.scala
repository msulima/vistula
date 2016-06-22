package pl.msulima.vistula.transpiler

package object rpn {

  sealed trait Token

  case class Reference(value: String) extends Token

  case class Constant(value: String) extends Token

  trait Operator {

    def apply(operands: List[Constant]): Constant
  }

  case class Operation(operator: Operator, operands: Seq[Token]) extends Token

}
