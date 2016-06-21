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
      s"${observables.head.value}.rxMap($$arg => (${operands.last.value}))"
    } else {
      s"vistula.zip(${ToArray.compact(observables.map(_.value))}).rxMap($$args => (${operands.last.value}))"
    }
    ConstantOperand(value)
  }
}

case object RxFlatMap extends ConstantOperator {

  override val operands = 2

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    ConstantOperand(s"${operands.head.value}.${operands(1).value}")
  }
}
