package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.control._
import pl.msulima.vistula.transpiler.dereferencer.data.{ClassDereferencer, ConstructorDereferencer, DictDereferencer, PrimitivesDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.modules.{ImportDereferencer, ReferenceDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.reference._
import pl.msulima.vistula.transpiler.dereferencer.template.{AttributesDereferencer, TemplateDereferencer}
import pl.msulima.vistula.transpiler.scope.Scope

trait Dereferencer {

  val scope: Scope
  val `package`: Package

  def dereference(expr: Ast.expr): Expression

  def dereference(stmt: Ast.stmt): Expression

  def dereference(token: Token): Expression

  def dereferenceStmt(program: Seq[Ast.stmt]): Seq[Expression] = {
    dereference(program.map(Direct.apply))
  }

  def dereference(program: Seq[Token]): Seq[Expression]
}

case class DereferencerImpl(scope: Scope, `package`: Package) extends Dereferencer
  with ArithmeticDereferencer
  with AttributesDereferencer
  with AssignDereferencer
  with BoxDereferencer
  with ClassDereferencer
  with ConstructorDereferencer
  with DeclareDereferencer
  with DictDereferencer
  with DereferenceDereferencer
  with FunctionDereferencer
  with FunctionCallDereferencer
  with GeneratorDereferencer
  with IfDereferencer
  with ImportDereferencer
  with LambdaDereferencer
  with LoopDereferencer
  with OperationDereferencer
  with ReferenceDereferencer
  with ReturnDereferencer
  with TemplateDereferencer
  with PrimitivesDereferencer {

  def dereference(program: Seq[Token]): Seq[Expression] = {
    Transformer.transform(program, scope, `package`)
  }

  override def dereference(expr: Ast.expr): Expression = dereference(Direct(Ast.stmt.Expr(expr)))

  override def dereference(stmt: Ast.stmt): Expression = dereference(Direct(stmt))

  override def dereference(token: Token): Expression = {
    declareDereferencer
      .orElse(arithmeticDereferencer)
      .orElse(assignDereferencer)
      .orElse(boxDereferencer)
      .orElse(dereferenceDereferencer)
      .orElse(dictDereferencer)
      .orElse(templateDereferencer)
      .orElse(functionDereferencer)
      .orElse(functionCallDereferencer)
      .orElse(generatorDereferencer)
      .orElse(ifDereferencer)
      .orElse(referenceDereferencer)
      .orElse(primitivesDereferencer)
      .orElse(lambdaDereferencer)
      .orElse(loopDereferencer)
      .orElse(operationDereferencer)
      .orElse(default)
      .apply(token)
  }

  private def default: PartialFunction[Token, Expression] = {
    case x: IdConstant =>
      IdConstant.expr(x.value)
    case x: TypedConstant =>
      ExpressionConstant(x.value, x.`type`)
  }
}
