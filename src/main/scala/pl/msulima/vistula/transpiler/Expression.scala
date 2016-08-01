package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.scope.{Identifier, ScopeElement}

sealed trait Expression {
  val `type`: ScopeElement
}

case class ExpressionConstant(value: String, `type`: ScopeElement) extends Expression

case class ExpressionOperation(operator: Operator, inputs: Seq[Expression], `type`: ScopeElement) extends Expression

case class ExpressionMap(output: Expression) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    Constant(s"$inputs, $output")
  }
}

object ExpressionConstant {
  val Dummy = ExpressionConstant("ignored", Identifier(observable = false))
}
