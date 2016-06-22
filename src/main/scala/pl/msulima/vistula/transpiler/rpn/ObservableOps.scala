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

case class RxMap(mutables: Seq[MutableOperand]) extends ConstantOperator {

  override val operands = 1

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    val value = if (mutables.size == 1) {
      s"${mutables.head.value}.rxMap($$arg => (${operands.last.value}))"
    } else {
      s"vistula.zip(${ToArray.compact(mutables.map(_.value))}).rxMap($$args => (${operands.last.value}))"
    }
    ConstantOperand(value)
  }
}

case object RxFlatMap extends ConstantOperator {

  override val operands = 2

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    ConstantOperand(s"${operands.head.value}.rxFlatMap($$arg => $$arg.${operands(1).value})")
  }
}
