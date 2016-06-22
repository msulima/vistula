package pl.msulima.vistula.transpiler

package object rpn {

  sealed trait Token

  case class MutableOperand(value: String) extends Token

  case class ConstantOperand(value: String) extends Token

  trait Operator {

    def apply(operands: List[ConstantOperand]): ConstantOperand
  }

  sealed trait Operation extends Token

  case class ConstantOperation(operator: Operator, operands: Seq[Token]) extends Operation

}
