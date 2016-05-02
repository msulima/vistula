package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object Transpiler {

  def wrap(program: Seq[Ast.stmt]): String = {
    if (program.size == 1) {
      apply(program.head)
    } else {
      s"""vistula.wrap(function () {
          |${Indent.leftPad(returnLast(program))}
          |})""".stripMargin
    }
  }

  def returnLast(program: Seq[Ast.stmt]): String = {
    val lines = apply(program.init) :+ s"return ${Transpiler(program.last)};"
    lines.mkString("\n")
  }

  def apply(program: Seq[Ast.stmt]): Seq[String] = {
    program.map(apply)
  }

  def apply: PartialFunction[Ast.stmt, String] = {
    Expression.apply.orElse(FunctionDef.apply).orElse(If.apply)
  }
}
