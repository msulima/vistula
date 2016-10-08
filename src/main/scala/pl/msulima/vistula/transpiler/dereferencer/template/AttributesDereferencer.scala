package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.data.{PrimitivesDereferencer, StaticArray, StaticString}
import pl.msulima.vistula.transpiler.dereferencer.reference.{BoxDereferencer, LambdaDereferencer}
import pl.msulima.vistula.transpiler.scope.{ClassDefinitionHelper, ScopeElement, Variable}

trait AttributesDereferencer {
  this: Dereferencer with BoxDereferencer with LambdaDereferencer =>

  def dereferenceAttribute(tag: parser.Tag): Expression = {
    StaticArray(tag.attributes.map({
      case parser.AttributeValue(key, value) =>
        tuple(key, toObservable(dereference(value)))
      case parser.AttributeMarker(key) =>
        tuple(key, toObservable(PrimitivesDereferencer.StaticNull))
      case parser.AttributeEvent(key, value) =>
        val ev = Ast.identifier("ev")
        val function = dereferenceLambda(Seq(Variable(ev, ScopeElement.const(ClassDefinitionHelper.Event))), Seq(Tokenizer.apply(value)))

        tuple(s"($key)", function)
    }))
  }

  private def tuple(key: String, body: Expression) = {
    StaticArray(Seq(StaticString(key), body))
  }
}
