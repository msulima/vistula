package pl.msulima.vistula.transpiler.rpn

object Transpiler extends App {

  def apply(token: Token): ConstantOperand = {
    token match {
      case ConstantOperation(Box, operands) =>
        apply(box(operands.head))
      case ConstantOperation(operation, operands) =>
        operation.apply(operands.map(apply).toList)
      case x: ConstantOperand => x
    }
  }

  def box(token: Token): Token = {
    token match {
      case MutableOperand(value) =>
        ConstantOperand(value)
      case _ =>
        val moved = Tokenizer.findAndReplace(token)
        moved match {
          case ConstantOperation(RxMap(_), _) =>
            moved
          case _ =>
            Box.apply(List(apply(moved)))
        }
    }
  }
}
