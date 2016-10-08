package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.expression.Other
import pl.msulima.vistula.transpiler.expression.reference._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

object Tokenizer {

  val Pass = TypedConstant("", ScopeElement.const(ClassReference.Unit))

  private val expr: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Expr(ex) =>
      apply(ex)
  }

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    expr
      .orElse(Declare.apply)
      .orElse(Other.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    Reference.apply
      .orElse({
        case e: Ast.expr => Direct(Ast.stmt.Expr(e))
      })
  }
}
