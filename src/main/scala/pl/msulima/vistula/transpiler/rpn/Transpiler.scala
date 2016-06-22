package pl.msulima.vistula.transpiler.rpn

object Transpiler extends App {

  def apply(token: Token): Constant = {
    token match {
      case Operation(Box, operands) =>
        apply(box(operands.head))
      case Operation(operation, operands) =>
        operation.apply(operands.map(apply).toList)
      case x: Constant => x
    }
  }

  def box(token: Token): Token = {
    token match {
      case Reference(value) =>
        Constant(value)
      case _ =>
        val moved = Tokenizer.findAndReplace(token)
        moved match {
          case Operation(RxMap(_), _) =>
            moved
          case _ =>
            Box.apply(List(apply(moved)))
        }
    }
  }
}
