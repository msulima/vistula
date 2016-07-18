package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.transpiler.rpn.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.rpn.expression.data.StaticArray
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.ToArray

object Attributes {

  def apply(tag: parser.Tag) = {
    ToArray(tag.attributes.map({
      case parser.AttributeValue(key, value) =>
        simple(key, Tokenizer.boxed(value))
      case parser.AttributeMarker(key) =>
        simple(key, Box(Constant("null")))
      case parser.AttributeEvent(key, value) =>
        val function = Operation(FunctionDef, Seq(Constant(""), Constant("ev")), Transformer.returnLast(Seq(Ast.stmt.Expr(value))))

        simple(s"($key)", function)
    }))
  }

  private def simple(key: String, body: Token) = {
    VistulaTranspiler(Operation(StaticArray, Seq(Constant(s""""$key""""), body), Tokenizer.Ignored))
  }
}
