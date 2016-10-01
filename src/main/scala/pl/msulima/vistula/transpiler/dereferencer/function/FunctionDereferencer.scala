package pl.msulima.vistula.transpiler.dereferencer.function

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionScope}
import pl.msulima.vistula.transpiler.expression.reference.Declare
import pl.msulima.vistula.transpiler.scope._

trait FunctionDereferencer {
  this: Dereferencer with ReturnDereferencer =>

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(func: FunctionDef, Nil) =>
      dereferenceFunction(func)
    case operation@Operation(FunctionScope, program) =>
      dereferenceScope(Seq(), program, box = true)
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

  def dereferenceFunction(func: FunctionDef): ExpressionOperation = {
    val body = dereferenceScope(func.arguments, func.program, box = false)
    val funcDefinition = FunctionDefinition(func.arguments.map(_.`type`), body.`type`)

    ExpressionOperation(func, Seq(body), ScopeElement.const(funcDefinition))
  }

  private def dereferenceScope(arguments: Seq[Variable], program: Seq[Token], box: Boolean): ExpressionOperation = {
    val result = Transformer.transform(arguments.map(ImportVariable) ++ program, scope, `package`)
    val maybeLast = findReturn(result, box)
    val body = result.init ++ maybeLast.toSeq

    ExpressionOperation(FunctionScope, body, body.last.`type`)
  }
}
