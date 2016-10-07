package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.FunctionCallDereferencer
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionDef2, FunctionScope}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{ExpressionOperation, _}

trait FunctionDereferencer {
  this: Dereferencer with ReturnDereferencer with FunctionCallDereferencer =>

  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(func: FunctionDef, Nil) =>
      dereferenceFunction(func)
  }

  def dereferenceAndAddToScope(func: Ast.stmt.FunctionDef) = {
    val functionDef = toFunctionDef(func)

    val id = functionDef.name
    val body = dereferenceFunction(functionDef)
    val wat = if (`package` == Package.Root) {
      body
    } else {
      // FIXME simplify
      dereference(Declare(id.toIdentifier, Operation(functionDef, Seq()), mutable = false, declare = false))
    }

    val ns = scope.addToScope(Variable(id.name, body.`type`))
    ScopedResult(ns, Seq(wat))
  }

  def toFunctionDef(func: Ast.stmt.FunctionDef): FunctionDef = {
    val arguments = FunctionDef.mapArguments(func.args)

    new FunctionDef(
      FunctionReference(`package`, func.name),
      func.body.map(Tokenizer.applyStmt),
      arguments
    )
  }

  def wrap(innerBody: Seq[Token]): ExpressionOperation = {
    wrap(dereferenceScope(innerBody))
  }

  def wrap(innerBody: Expression): ExpressionOperation = {
    val func = FunctionDef(FunctionReference.Anonymous, Seq(), Seq())
    val funcDefinition = FunctionDefinition(Seq(), innerBody.`type`)
    val innerFunction = ExpressionOperation(func, Seq(innerBody), ScopeElement.const(funcDefinition))

    functionCall(Wrap, Seq(innerFunction))
  }

  def anonymousFunction(arguments: Seq[Variable], body: Seq[Token]): ExpressionOperation = {
    dereferenceFunction(FunctionReference.Anonymous, arguments, body)
  }

  def dereferenceFunction(func: FunctionDef): ExpressionOperation = {
    dereferenceFunction(func.name, func.arguments, func.program)
  }

  private def dereferenceFunction(name: FunctionReference, arguments: Seq[Variable], program: Seq[Token]): ExpressionOperation = {
    val body = dereferenceScope(arguments.map(ImportVariable) ++ program)

    val func = FunctionDef2(name, arguments)
    val funcDefinition = FunctionDefinition(arguments.map(_.`type`), body.`type`)

    ExpressionOperation(func, Seq(body), ScopeElement.const(funcDefinition))
  }

  def dereferenceScope(program: Seq[Token]): ExpressionOperation = {
    val result = dereference(program)
    val maybeLast = findReturn(result, box = false)
    val body = result.init ++ maybeLast.toSeq
    val returnType = body.lastOption.map(_.`type`).getOrElse(ScopeElement.const(ClassReference.Unit))

    ExpressionOperation(FunctionScope, body, returnType)
  }
}
