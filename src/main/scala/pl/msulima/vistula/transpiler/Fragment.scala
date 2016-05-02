package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr


case class Fragment(code: String, dependencies: Seq[Ast.stmt] = Seq(), useFlatMap: Boolean = false) {

  def mapper = {
    if (useFlatMap) {
      "flatMap"
    } else {
      "map"
    }
  }
}

object Fragment {

  def apply(expressions: Seq[Ast.expr], useFlatMap: Boolean)(f: List[String] => String): Fragment = {
    val operands = Operands(expressions)

    val code = f(operands.map(_._2).toList)
    val dependsOn = operands.flatMap(_._1)

    Fragment(code, dependsOn.map(Ast.stmt.Expr), useFlatMap)
  }
}

object Operands {

  def apply(expressions: Seq[Ast.expr]): Seq[(Option[expr], String)] = {
    val xs = expressions.map({
      case Ast.expr.Str(x) => Left("\"" + x + "\"")
      case Ast.expr.Num(x) => Left(x.toString)
      case x => Right(x)
    })

    if (xs.count(_.isRight) == 1) {
      xs.map({
        case Left(x) => (None, x)
        case Right(x) => (Some(x), "$arg")
      })
    } else {
      xs.zipWithIndex.map({
        case (Left(x), idx) => (None, x)
        case (Right(x), idx) => (Some(x), s"$$args[$idx]")
      })
    }
  }
}
