package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer

trait DereferenceDereferencer {
  this: Dereferencer =>

  def dereferenceDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Dereference(value))) =>
      val dereferenced = dereference(value)

      toConstant(dereferenced)
  }

  def toConstant(expression: Expression): Expression = {
    if (expression.`type`.observable) {
      ExpressionOperation(Dereference, Seq(expression), expression.`type`.copy(observable = false))
    } else {
      expression
    }
  }
}

case object Dereference extends Operator {

  override def apply(operands: List[Constant]): String = {
    s"${operands.head.value}.rxLastValue()"
  }
}
