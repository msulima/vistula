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
        if (scope.functions.contains(input)) {
          ExpressionConstant(input.asInstanceOf[Constant].value, scope.functions(input))
        } else if (scope.isKnownStatic(Ast.identifier(id))) {
          ExpressionConstant(id, Identifier(observable = false))
        } else {
          ExpressionConstant(id, Identifier(observable = true))
        }
      case _ =>
        dereference(input)
    }
  }

  def referenceField(source: Expression, target: Token): ExpressionOperation = {
    source match {
      case ExpressionConstant(_, id: Identifier) if id.observable =>
        val body = ExpressionOperation(Reference, Seq(source, dereference(target)), id)

        ExpressionOperation(ExpressionFlatMap(body), Seq(source), id)
      case ExpressionOperation(_, _, id: Identifier) if id.observable =>
        val body = ExpressionOperation(Reference, Seq(source, dereference(target)), id)

        ExpressionOperation(ExpressionFlatMap(body), Seq(source), id)
    }
  }

  private def getType(id: Identifier, output: Constant) = {
    val clazz = scope.classes(Constant(id.`type`.name))

    clazz.fields.get(Ast.identifier(output.value))
  }
}
