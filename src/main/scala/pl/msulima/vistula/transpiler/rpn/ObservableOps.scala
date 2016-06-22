package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.util.ToArray

case object Box extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    operands match {
      case value :: Nil =>
        Constant(s"vistula.constantObservable(${value.value})")
    }
  }
}

case class RxMap(mutables: Seq[Reference]) extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    val value = if (mutables.size == 1) {
      s"${mutables.head.value}.rxMap($$arg => (${operands.last.value}))"
    } else {
      s"vistula.zip(${ToArray.compact(mutables.map(_.value))}).rxMap($$args => (${operands.last.value}))"
    }
    Constant(value)
  }
}

case object RxFlatMap extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    Constant(s"${operands.head.value}.rxFlatMap($$arg => $$arg.${operands(1).value})")
  }
}
