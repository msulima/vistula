package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.control.{FunctionDef, FunctionDef2}
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, FunctionReference, ScopeElement, Variable}

trait LambdaDereferencer {
  this: Dereferencer =>

  def lambdaDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Lambda(args, body))) =>
      val arguments = FunctionDef.mapArguments(args)
      dereferenceLambda(arguments, Seq(Tokenizer.apply(body)))
  }

  def dereferenceLambda(arguments: Seq[Variable], body: Seq[Token]): ExpressionOperation = {
    val dereferencer = DereferencerImpl(scope.addToScope(arguments), `package`)
    val transpiledBody = dereferencer.dereferenceScope(body)

    dereferenceLambda(arguments, transpiledBody)
  }

  def dereferenceLambda(arguments: Seq[Variable], body: Expression): ExpressionOperation = {
    val func = FunctionDef2(FunctionReference.Anonymous, arguments)
    val funcDefinition = FunctionDefinition(arguments.map(_.`type`), body.`type`)

    ExpressionOperation(func, Seq(body), ScopeElement.const(funcDefinition))
  }
}
