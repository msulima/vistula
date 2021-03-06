package pl.msulima.vistula.transpiler.dereferencer.modules

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait ReferenceDereferencer {
  this: Dereferencer =>

  def referenceDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Name(id, Ast.expr_context.Load))) if !Seq("None", "False", "True").contains(id.name) =>
      referenceSingle(IdConstant(id))
    case Direct(Ast.stmt.Expr(Ast.expr.Attribute(expr, id, Ast.expr_context.Load))) =>
      dereference(Reference(Tokenizer.apply(expr), id))
    case Operation(Reference, IdConstant(input) :: Nil) =>
      referenceSingle(IdConstant(input))
    case op@Operation(Reference, source :: target :: Nil) =>
      val dereferencedSource = referenceSingle(source)

      referenceField(dereferencedSource, target)
  }

  private def referenceSingle(input: Token): Expression = {
    input match {
      case IdConstant(id) =>
        val scopeElement = scope.findById(id)
        ExpressionConstant(id.name, scopeElement.getOrElse(ScopeElement.Default))
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
    val targetExpr = target.asInstanceOf[IdConstant]

    val maybeTypedOperation = for {
      fieldType <- scope.findClass(sourceType).fields.get(targetExpr.value)
    } yield {
      ExpressionOperation(Reference, Seq(source, ExpressionConstant(targetExpr.value.name, fieldType)), fieldType)
    }

    maybeTypedOperation.getOrElse(
      ExpressionOperation(Reference, Seq(source, dereference(target)), sourceElement)
    )
  }
}


case object Reference extends Operator {

  def apply(id: Ast.identifier): Token = {
    Operation(Reference, Seq(IdConstant(id)))
  }

  def apply(source: Token, attribute: Ast.identifier): Token = {
    Operation(Reference, Seq(source, IdConstant(attribute)))
  }

  override def apply(operands: List[Constant]): String = {
    if (operands.size == 1) {
      operands.head.value
    } else {
      s"${operands.head.value}.${operands(1).value}"
    }
  }
}
