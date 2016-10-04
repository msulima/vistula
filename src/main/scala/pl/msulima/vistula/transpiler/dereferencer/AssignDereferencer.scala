package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait AssignDereferencer {
  this: Dereferencer =>

  def assignDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.AssignStmt(expr, value)) =>
      val tokens = Seq(Tokenizer.apply(expr), Box(Tokenizer.applyStmt(value)))
      ExpressionOperation(Assign, tokens.map(dereference), ScopeElement.const(ClassReference.Unit))
  }
}

case object Assign extends Operator {

  override def apply(operands: List[Constant]) = {
    s"${operands(1).value}.rxForEachOnce($$arg => ${operands(0).value}.rxPush($$arg))"
  }
}
