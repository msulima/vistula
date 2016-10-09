package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference
import pl.msulima.vistula.transpiler.dereferencer.reference.{DeclareDereferencer, FunctionCallDereferencer}
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{ExpressionOperation, _}

trait FunctionDereferencer {
  this: Dereferencer with ReturnDereferencer with FunctionCallDereferencer with DeclareDereferencer =>

  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(func: FunctionDef, Nil) =>
      dereferenceFunction(func)
  }

  def dereferenceAndAddToScope(func: Ast.stmt.FunctionDef) = {
    val id = func.name
    val body = dereferenceFunction(toFunctionDef(func))
    val declaration = if (`package` == Package.Root) {
      body
    } else {
      dereferenceDeclare(IdConstant.expr(FunctionReference(`package`, id).toIdentifier), body, mutable = false, declare = false)
    }

    ScopedResult(scope.addToScope(Variable(id, body.`type`)), Seq(declaration))
  }

  def toFunctionDef(func: Ast.stmt.FunctionDef): FunctionDef = {
    val arguments = FunctionDef.mapArguments(func.args)

    new FunctionDef(
      FunctionReference(`package`, func.name),
      func.body.map(Tokenizer.applyStmt),
      arguments
    )
  }

  def wrap(body: Expression): Expression = {
    functionCall(Wrap, Seq(functionOperation(FunctionReference.Anonymous, Seq(), body)))
  }

  def dereferenceFunction(func: FunctionDef): ExpressionOperation = {
    dereferenceFunction(func.name, func.arguments, func.program)
  }

  def dereferenceFunction(name: FunctionReference, arguments: Seq[Variable], program: Seq[Token]): ExpressionOperation = {
    val body = reduceToScope(dereference(arguments.map(ImportVariable) ++ program))

    functionOperation(name, arguments, body)
  }

  def functionOperation(name: FunctionReference, arguments: Seq[Variable], body: Expression) = {
    val func = FunctionDef2(name, arguments)
    val funcDefinition = FunctionDefinition(arguments.map(_.`type`), body.`type`)

    ExpressionOperation(func, Seq(body), ScopeElement.const(funcDefinition))
  }

  def reduceToScope(result: Seq[Expression]): ExpressionOperation = {
    val maybeLast = findReturn(result, box = false)
    val body = result.init ++ maybeLast.toSeq
    val returnType = body.lastOption.map(_.`type`).getOrElse(ScopeElement.const(ClassReference.Unit))

    ExpressionOperation(FunctionScope, body, returnType)
  }
}
