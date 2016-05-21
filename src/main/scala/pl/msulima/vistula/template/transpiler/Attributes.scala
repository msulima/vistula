package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.ToArray

object Attributes {

  def apply(tag: parser.Tag) = {
    ToArray(tag.attributes.map({
      case parser.AttributeValue(key, value) =>
        s"""["$key", ${VistulaTranspiler(Ast.stmt.Expr(value))}]"""
      case parser.AttributeMarker(key) =>
        s"""["$key", ${VistulaTranspiler(Ast.stmt.Expr(Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load)))}]"""
      case parser.AttributeEvent(key, value) =>
        val arguments = Ast.arguments(Seq(Ast.expr.Name(Ast.identifier("ev"), Ast.expr_context.Param)), None, None, Seq())
        val function = Ast.stmt.FunctionDef(Ast.identifier(""), arguments, Seq(Ast.stmt.Expr(value)), Seq())

        s"""["($key)", ${VistulaTranspiler(function)}]"""
    }))
  }
}
