package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.ToArray

object Primitives {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Num(x) =>
      Fragment(s"vistula.constantObservable(${x.toString})")
    case Ast.expr.Str(x) =>
      Fragment(s"""vistula.constantObservable("$x")""")
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      Fragment(s"""vistula.constantObservable(${ToArray(elts.map(x => Transpiler.apply(Ast.stmt.Expr(x))))})""")
  }
}
