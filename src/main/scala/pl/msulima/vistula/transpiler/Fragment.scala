package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr


case class Fragment(code: String, dependencies: Seq[Ast.stmt] = Seq())

object Fragment {

  def apply(expressions: Seq[Ast.expr])(f: Seq[String] => String): Fragment = {
    val operands = Operands(expressions)

    val code = f(operands.map(_._2))
    val dependsOn = operands.flatMap(_._1)

    Fragment(code, dependsOn.map(Ast.stmt.Expr))
  }
}

object Operands {

  def apply(expressions: Seq[Ast.expr]): List[(Option[expr], String)] = {
    val init = (Seq.empty[(Option[Ast.expr], String)], 0)

    expressions.foldLeft(init)((acc, expr) => {
      expr match {
        case Ast.expr.Str(x) => (acc._1 :+(None, "\"" + x + "\""), acc._2)
        case Ast.expr.Num(x) => (acc._1 :+(None, x.toString), acc._2)
        case _ => (acc._1 :+(Some(expr), s"$$args[${acc._2}]"), acc._2 + 1)
      }
    })._1.toList
  }
}
