package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.ScopeElement
import pl.msulima.vistula.util.ToArray

sealed trait Expression {
  val `type`: ScopeElement
}

case class ExpressionConstant(value: String, `type`: ScopeElement) extends Expression

case class ExpressionOperation(operator: Operator, inputs: Seq[Expression], `type`: ScopeElement) extends Expression {

  override def toString: String =
    operator match {
      case _: FunctionCall =>
        s"FunctionCall(${inputs.head})(${inputs.tail.mkString(".")})"
      case Reference =>
        s"Reference(${inputs.mkString(".")})"
      case RxMap(body) =>
        s"RxMap($body)"
      case RxFlatMap(body) =>
        s"RxFlatMap($body)"
      case _ =>
        s"ExpressionOperation($operator, $inputs, ${`type`})"
    }
}

case class RxMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant]): String = {
    RxMapOp(useFlatMap = false, inputs.head, inputs.tail)
  }
}

case class RxFlatMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant]): String = {
    RxMapOp(useFlatMap = true, inputs.head, inputs.tail)
  }
}

object RxMapOp {

  def apply(useFlatMap: Boolean, body: Constant, operands: List[Constant]) = {
    val mapper = if (useFlatMap) {
      "rxFlatMap"
    } else {
      "rxMap"
    }

    if (operands.isEmpty) {
      body.value
    } else if (operands.size == 1) {
      s"${operands.head.value}.$mapper($$arg => (${body.value}))"
    } else {
      s"vistula.zip(${ToArray(operands.map(_.value))}).$mapper($$args => (${body.value}))"
    }
  }
}
