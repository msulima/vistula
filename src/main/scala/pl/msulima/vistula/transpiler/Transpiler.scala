package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object Transpiler {

  val EmptyScope = new Scope(Seq(), Seq(), mutable = true)

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
    val lines = apply(program.init) :+ s"return ${Transpiler(program.last)}"
    lines.mkString("", ";\n", ";\n")
  }

  def apply(program: Seq[Ast.stmt]): Seq[String] = {
    program.map(x => apply(x))
  }

  def apply(expr: Ast.expr): String = {
    apply(Ast.stmt.Expr(expr))
  }

  def apply(stmt: Ast.stmt): String = {
    scoped(EmptyScope, stmt).asCodeObservable
  }

  def scoped(program: Seq[Ast.stmt]): String = {
    program.foldLeft(ScopedResult(EmptyScope, Result("", mutable = false)))((acc, stmt) => {
      val result = scoped(acc.scope, stmt)

      result.copy(result = acc.result.copy(code = acc.result.code + result.asCodeObservable + ";\n"))
    }).result.code.dropRight(1)
  }

  def scoped(scope: Scope, expr: Ast.expr): ScopedResult = {
    scoped(scope, Ast.stmt.Expr(expr))
  }

  def scoped(scope: Scope, stmt: Ast.stmt): ScopedResult = {
    val base = FunctionDef.apply.orElse(If.apply).orElse(Loop.apply).andThen(code => scope(code))

    base.orElse(Expression.apply(scope).orElse(Assign.apply(scope)))(stmt)
  }
}
