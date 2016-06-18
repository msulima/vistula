package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Fragment, RxFlatMap, Transpiler}

object FunctionCall {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      val x: Seq[String] = args.map(arg => Transpiler(arg))

      Fragment(s"$func(${x.mkString(", ")})", RxFlatMap, Seq())
    case Ast.expr.Call(func, args, _, _, _) =>
      val x: Seq[String] = args.map(arg => Transpiler(arg))

      Fragment(s"%s(${x.mkString(", ")})", RxFlatMap, Seq(func))
  }
}
