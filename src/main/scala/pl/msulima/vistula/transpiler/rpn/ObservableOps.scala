package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.util.ToArray

case object Box extends ConstantOperator {

  override val operands: Int = 1

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    operands match {
      case value :: Nil =>
        ConstantOperand(s"vistula.constantObservable(${value.value})")
    }
  }
}

case class RxMap(observablesCount: Int) extends ConstantOperator {

  override val operands = observablesCount + 1

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    val observables = operands.take(observablesCount)

    val value = if (observablesCount == 1) {
      s"${observables.head.value}.map($$arg => (${operands.last.value}))"
    } else {
      s"vistula.zip(${ToArray.compact(observables.map(_.value))}).map($$args => (${operands.last.value}))"
    }
    ConstantOperand(value)
  }
}
