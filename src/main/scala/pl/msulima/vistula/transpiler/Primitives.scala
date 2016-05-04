package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.ToArray

object Primitives {

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineJavascriptPrefix) =>
      Fragment(x.stripPrefix(MagicInlineJavascriptPrefix))
    case Ast.expr.Num(x) =>
      Fragment(s"vistula.constantObservable(${x.toString})")
    case Ast.expr.Name(Ast.identifier("false"), Ast.expr_context.Load) =>
      Fragment(s"""vistula.constantObservable(false)""")
    case Ast.expr.Name(Ast.identifier("true"), Ast.expr_context.Load) =>
      Fragment(s"""vistula.constantObservable(true)""")
    case Ast.expr.Str(x) =>
      Fragment(s"""vistula.constantObservable("$x")""")
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      Fragment(s"""vistula.constantObservable(${ToArray(elts.map(x => Transpiler.apply(Ast.stmt.Expr(x))))})""")
    case Ast.expr.Dict(keys, values) =>
      val dict = keys.zip(values).map({
        case (Ast.expr.Str(key), value) =>
          (s""""$key"""", Transpiler(Ast.stmt.Expr(value)))
      })
      Fragment(s"""vistula.constantObservable(${ToArray.toDict(dict)})""")
  }
}
