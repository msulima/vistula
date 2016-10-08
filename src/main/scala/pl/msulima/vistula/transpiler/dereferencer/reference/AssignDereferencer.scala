package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait AssignDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def assignDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.AssignStmt(expr, value)) =>
      val tokens = Seq(dereference(expr), toObservable(dereference(value)))

      ExpressionOperation(Assign, tokens, ScopeElement.const(ClassReference.Unit))
  }
}

case object Assign extends Operator {

  override def apply(operands: List[Constant]) = {
    s"${operands(1).value}.rxForEachOnce($$arg => ${operands(0).value}.rxPush($$arg))"
  }
}
