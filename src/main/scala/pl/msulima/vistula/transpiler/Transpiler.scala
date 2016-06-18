package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object Transpiler {

  val EmptyScope = new Scope(Seq(), Seq(), null)

  def wrap(program: Seq[Ast.stmt]): String = {
    wrap(EmptyScope, program)
  }

  def wrap(scope: Scope, program: Seq[Ast.stmt]): String = {
    if (program.size == 1) {
      apply(scope: Scope, program.head)
    } else {
      s"""vistula.wrap(() => {
          |${Indent.leftPad(returnLast(scope, program))}
          |})""".stripMargin
    }
  }

  def returnLast(program: Seq[Ast.stmt]): String = {
    returnLast(EmptyScope, program)
  }

  def returnLast(scope: Scope, program: Seq[Ast.stmt]): String = {
    val lines = apply(scope, program.init) :+ s"return ${Transpiler(scope, program.last)}"
    lines.mkString("", ";\n", ";\n")
  }

  def apply(program: Seq[Ast.stmt]): Seq[String] = {
    program.map(x => apply(EmptyScope, x))
  }

  def apply(scope: Scope, program: Seq[Ast.stmt]): Seq[String] = {
    program.map(x => apply(scope, x))
  }

  def apply(scope: Scope, expr: Ast.expr): String = {
    apply(scope, Ast.stmt.Expr(expr))
  }

  def apply(expr: Ast.expr): String = {
    apply(EmptyScope, Ast.stmt.Expr(expr))
  }

  def apply(stmt: Ast.stmt): String = {
    apply(EmptyScope, stmt)
  }

  def apply(scope: Scope, stmt: Ast.stmt): String = {
    Expression.apply.orElse(FunctionDef.apply).orElse(If.apply).orElse(Loop.apply).orElse(Assign.apply(scope: Scope))(stmt)
  }
}
