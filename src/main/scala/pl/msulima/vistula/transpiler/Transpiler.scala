package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Transpiler {

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
