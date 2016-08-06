package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{ClassDefinition, ScopeElement}

trait ReferenceDereferencer {
  this: Dereferencer =>

  def referenceDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Reference, Constant(input) :: Nil, _, _) =>
      referenceSingle(Constant(input))
    case op@Operation(Reference, source :: target :: Nil, _, _) =>
      val dereferencedSource = referenceSingle(source)

      referenceField(dereferencedSource, target)
  }

  private def referenceSingle(input: Token): Expression = {
    input match {
      case Constant(id) =>
        val scopeElement = scope.findById(Ast.identifier(id))
        ExpressionConstant(id, scopeElement.getOrElse(ScopeElement(observable = true)))
      case _ =>
        dereference(input)
    }
  }

  def referenceField(source: Expression, target: Token): Expression = {
    val sourceElement = source.`type`.asInstanceOf[ScopeElement]
    val sourceType = sourceElement.`type`.asInstanceOf[ClassDefinition]

    if (sourceElement.observable) {
      val body = ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)

      ExpressionOperation(ExpressionFlatMap(body), Seq(source), sourceElement)
    } else {
      val targetExpr = target.asInstanceOf[Constant]

      val maybeTypedOperation = for {
        fieldType <- sourceType.fields.get(Ast.identifier(targetExpr.value))
      } yield {
        ExpressionOperation(Reference, Seq(source, ExpressionConstant(targetExpr.value, fieldType)), fieldType)
      }

      maybeTypedOperation.getOrElse(
        ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)
      )
    }
  }
}
