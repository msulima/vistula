package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.transpiler.rpn.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.rpn.expression.data.{StaticArray, StaticString}

object Attributes {

  def apply(tag: parser.Tag) = {
    StaticArray(tag.attributes.map({
      case parser.AttributeValue(key, value) =>
        tuple(key, Tokenizer.boxed(value))
      case parser.AttributeMarker(key) =>
        tuple(key, Box(Constant("null")))
      case parser.AttributeEvent(key, value) =>
        val function = Operation(FunctionDef, Seq(Constant(""), Constant("ev")), Transformer.returnLast(Seq(Ast.stmt.Expr(value))))

        tuple(s"($key)", function)
    }))
  }

  private def tuple(key: String, body: Token) = {
    StaticArray(Seq(StaticString(key), body))
  }
}
