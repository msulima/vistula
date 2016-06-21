package pl.msulima.vistula.transpiler

package object rpn {

  sealed trait Token


  sealed trait Operand extends Token {
    val value: String
  }

  case class MutableOperand(value: String) extends Operand

  case class ConstantOperand(value: String) extends Operand

  sealed trait Operator extends Token

  trait ConstantOperator extends Operator {

    val operands: Int

    def apply(operands: List[ConstantOperand]): ConstantOperand
  }

}
