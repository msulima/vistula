package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.scope.{ClassDefinitionHelper, ScopeElement, Variable}

object Attributes {

  def apply(tag: parser.Tag) = {
    StaticArray(tag.attributes.map({
      case parser.AttributeValue(key, value) =>
        tuple(key, Box(Tokenizer.apply(value)))
      case parser.AttributeMarker(key) =>
        tuple(key, Box(Constant("null")))
      case parser.AttributeEvent(key, value) =>
        val ev = Ast.identifier("ev")
        val function = FunctionDef.anonymous(
          Variable(ev, ScopeElement(observable = false, ClassDefinitionHelper.Event)),
          Seq(Tokenizer.apply(value))
        )

        tuple(s"($key)", function)
    }))
  }

  private def tuple(key: String, body: Token) = {
    StaticArray(Seq(StaticString(key), body))
  }
}
