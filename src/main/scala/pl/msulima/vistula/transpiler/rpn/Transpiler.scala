package pl.msulima.vistula.transpiler.rpn

object Transpiler extends App {

  def apply(token: Token): ConstantOperand = {
    token match {
      case ConstantOperation(operation, operands) =>
        operation.apply(operands.map(apply).toList)
      case x: ConstantOperand => x
    }
  }
}
