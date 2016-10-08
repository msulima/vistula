package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

object Tokenizer {

  val Pass = TypedConstant("", ScopeElement.const(ClassReference.Unit))

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    case stmt: Ast.stmt =>
      Direct(stmt)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    case e: Ast.expr =>
      Direct(Ast.stmt.Expr(e))
  }
}
