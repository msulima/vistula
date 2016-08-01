package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.scope.ScopeElement

sealed trait Expression

case class ExpressionConstant(value: String, `type`: ScopeElement) extends Expression

case class ExpressionOperation(operator: Operator, inputs: Seq[Expression], `type`: ScopeElement) extends Expression

case class ExpressionMap(output: Expression) extends Operator {

  override def apply(inputs: List[Constant], output: Constant): Constant = {
    Constant(s"$inputs, $output")
  }
}
