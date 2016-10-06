package pl.msulima.vistula.transpiler.dereferencer.modules

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
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
        ExpressionConstant(id, scopeElement.getOrElse(ScopeElement.Default))
      case _ =>
        dereference(input)
    }
  }

  private def referenceField(source: Expression, target: Token): Expression = {
    val sourceElement = source.`type`
    val body = referenceConstantField(source, target, sourceElement)

    val observables = if (sourceElement.observable) {
      Seq(source)
    } else {
      Seq()
    }
    RxMapOp(observables, body)
  }

  private def referenceConstantField(source: Expression, target: Token, sourceElement: ScopeElement): ExpressionOperation = {
    val sourceType = sourceElement.`type`.asInstanceOf[ClassReference]
    val targetExpr = target.asInstanceOf[Constant]

    val maybeTypedOperation = for {
      fieldType <- scope.findClass(sourceType).fields.get(Ast.identifier(targetExpr.value))
    } yield {
      ExpressionOperation(Reference, Seq(source, ExpressionConstant(targetExpr.value, fieldType)), fieldType)
    }

    maybeTypedOperation.getOrElse(
      ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)
    )
  }
}