package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Transpiler {

  val EmptyScope = Scope(Seq(), Seq(), mutable = false)

  def apply(program: Seq[Ast.stmt]): String = {
    scoped(EmptyScope, program).result.code.dropRight(1)
  }

  def apply(expr: Ast.expr): String = {
    apply(Ast.stmt.Expr(expr))
  }

  def apply(stmt: Ast.stmt): String = {
    scoped(EmptyScope, stmt).asCodeObservable
  }

  def scoped(scope: Scope, program: Seq[Ast.stmt]): ScopedResult = {
    program.foldLeft(ScopedResult(scope, Result("", mutable = false)))((acc, stmt) => {
      val result = scoped(acc.scope, stmt)

      result.copy(result = acc.result.copy(code = acc.result.code + result.asCodeObservable + ";\n"))
    })
  }

  def scoped(scope: Scope, stmt: Ast.stmt): ScopedResult = {
    Expression.apply(scope)(stmt)
  }
}
