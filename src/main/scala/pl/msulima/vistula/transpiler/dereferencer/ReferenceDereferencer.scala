package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait ReferenceDereferencer {
  this: Dereferencer =>

  def referenceDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Reference, Constant(input) :: Nil) =>
      referenceSingle(Constant(input))
    case op@Operation(Reference, source :: target :: Nil) =>
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

  private def referenceField(source: Expression, target: Token): Expression = {
    val sourceElement = source.`type`

    if (sourceElement.observable) {
      val body = ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)

      ExpressionOperation(RxFlatMap(body), Seq(source), sourceElement)
    } else {
      referenceConstantField(source, target, sourceElement)
    }
  }

  private def referenceConstantField(source: Expression, target: Token, sourceElement: ScopeElement): ExpressionOperation = {
    val sourceType = sourceElement.`type`.asInstanceOf[ClassReference]
    val targetExpr = target.asInstanceOf[Constant]

    val maybeTypedOperation = for {
      fieldType <- scope.classes(sourceType).fields.get(Ast.identifier(targetExpr.value))
    } yield {
      ExpressionOperation(Reference, Seq(source, ExpressionConstant(targetExpr.value, fieldType)), fieldType)
    }

    maybeTypedOperation.getOrElse(
      ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)
    )
  }
}
