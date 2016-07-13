package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object Transpiler {

  val EmptyScope = new Scope(Seq(), Seq(), mutable = false)

  def wrap(program: Seq[Ast.stmt]): String = {
    if (program.size == 1) {
      apply(program.head)
    } else {
      s"""vistula.wrap(() => {
          |${Indent.leftPad(returnLast(program))}
          |})""".stripMargin
    }
  }

  def returnLast(program: Seq[Ast.stmt]): String = {
    val nextScope = scoped(EmptyScope, program.init)
    nextScope.result.code + s"return ${scoped(nextScope.scope, program.last).result.code};"
  }

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

  def scoped(scope: Scope, expr: Ast.expr): ScopedResult = {
    scoped(scope, Ast.stmt.Expr(expr))
  }

  def scoped(scope: Scope, stmt: Ast.stmt): ScopedResult = {
    val base = FunctionDef.apply.andThen(code => scope(code))

    base.orElse(Expression.apply(scope))(stmt)
  }
}
