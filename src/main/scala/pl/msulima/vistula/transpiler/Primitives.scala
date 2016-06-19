package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.ToArray

object Primitives {

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def apply(scope: Scope): PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineJavascriptPrefix) =>
      CodeTemplate(x.stripPrefix(MagicInlineJavascriptPrefix), RxMap)
    case expr: Ast.expr if static(scope).isDefinedAt(expr) =>
      CodeTemplate(static(scope)(expr), Static)
  }

  def static(scope: Scope): PartialFunction[Ast.expr, String] = {
    case Ast.expr.Num(x) =>
      x.toString
    case Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load) =>
      "null"
    case Ast.expr.Name(Ast.identifier("False"), Ast.expr_context.Load) =>
      "false"
    case Ast.expr.Name(Ast.identifier("True"), Ast.expr_context.Load) =>
      "true"
    case Ast.expr.Str(x) =>
      s""""$x""""
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      ToArray(elts.map(x => Transpiler.scoped(scope, x).asCodeObservable))
    case Ast.expr.Dict(keys, values) =>
      val dict = keys.zip(values).map({
        case (Ast.expr.Str(key), value) =>
          (s""""$key"""", Transpiler.scoped(scope, value).asCodeObservable)
      })
      ToArray.toDict(dict)
  }
}
