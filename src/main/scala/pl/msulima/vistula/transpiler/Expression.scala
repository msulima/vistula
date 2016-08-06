package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.scope.ScopeElement

sealed trait Expression {
  val `type`: ScopeElement
}

case class ExpressionConstant(value: String, `type`: ScopeElement) extends Expression

case class ExpressionOperation(operator: Operator, inputs: Seq[Expression], `type`: ScopeElement) extends Expression

case class ExpressionMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    RxMapOp(useFlatMap = false).apply(inputs, output)
  }
}

case class ExpressionFlatMap(output: ExpressionOperation) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    RxMapOp(useFlatMap = true).apply(inputs, output)
  }
}

object ExpressionConstant {
  val Dummy = ExpressionConstant("ignored", ScopeElement(observable = false))
}
