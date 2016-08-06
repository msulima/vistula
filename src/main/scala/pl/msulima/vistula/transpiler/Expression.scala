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
      case FunctionCall =>
        s"FunctionCall(${inputs.head})(${inputs.tail.mkString(".")})"
      case Reference =>
        s"Reference(${inputs.mkString(".")})"
      case ExpressionMap(body) =>
        s"Map($body)"
      case ExpressionFlatMap(body) =>
        s"FlatMap($body)"
      case _ =>
        s"ExpressionOperation($operator, $inputs, ${`type`})"
    }
}

case class ExpressionMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    RxMapOp(useFlatMap = false, inputs, output)
  }
}

case class ExpressionFlatMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    RxMapOp(useFlatMap = true, inputs, output)
  }
}

object RxMapOp {

  def apply(useFlatMap: Boolean, operands: List[Constant], output: Constant): Constant = {
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

object ExpressionConstant {
  val Dummy = ExpressionConstant("ignored", ScopeElement(observable = false))
}
