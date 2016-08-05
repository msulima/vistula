package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.Identifier

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
        ExpressionConstant(id, scopeElement.getOrElse(Identifier(observable = true)))
      case _ =>
        dereference(input)
    }
  }

  def referenceField(source: Expression, target: Token): Expression = {
    val sourceType = source.`type`.asInstanceOf[Identifier]

    if (sourceType.observable) {
      val body = ExpressionOperation(Reference, Seq(source, dereference(target)), sourceType)

      ExpressionOperation(ExpressionFlatMap(body), Seq(source), sourceType)
    } else {
      val maybeTypedOperation = for {
        sourceType <- scope.classes.get(sourceType.`type`)
      } yield {
        val targetExpr = target.asInstanceOf[Constant]
        val fieldType = sourceType.fields(Ast.identifier(targetExpr.value))

        ExpressionOperation(Reference, Seq(source, ExpressionConstant(targetExpr.value, fieldType)), fieldType)
      }

      maybeTypedOperation.getOrElse(source)
    }
  }
}
