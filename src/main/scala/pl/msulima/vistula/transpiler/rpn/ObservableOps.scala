package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.util.{Indent, ToArray}

case object BoxOp extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"vistula.constantObservable(${output.value})")
  }
}

case object Noop extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    output
  }
}

case object Wrap extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    if (operands.size == 1) {
      operands.head
    } else {
      Constant(
        s"""vistula.wrap(() => {
            |${Indent.leftPad(Transpiler.toJavaScript(operands))}
            |})""".stripMargin)
    }
  }
}

case object UnboxOp extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${output.value}.rxLastValue()")
  }
}

case class RxMapOp(useFlatMap: Boolean) extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val mapper = if (useFlatMap) {
      "rxFlatMap"
    } else {
      "rxMap"
    }

    val value = if (operands.isEmpty) {
      output.value
    } else if (operands.size == 1) {
      s"${operands.head.value}.$mapper($$arg => (${output.value}))"
    } else {
      s"vistula.zip(${ToArray(operands.map(_.value))}).$mapper($$args => (${output.value}))"
    }
    Constant(value)
  }
}
