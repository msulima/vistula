package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.control.{FunctionDef, FunctionDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.scope.{FunctionReference, Variable}

trait LambdaDereferencer {
  this: Dereferencer with FunctionDereferencer =>

  def lambdaDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Lambda(args, body))) =>
      val arguments = FunctionDef.mapArguments(args)
      dereferenceLambda(arguments, Seq(Tokenizer.apply(body)))
  }

  def dereferenceLambda(arguments: Seq[Variable], body: Seq[Token]): ExpressionOperation = {
    val dereferencer = DereferencerImpl(scope.addToScope(arguments), `package`)
    val transpiledBody = dereferencer.reduceToScope(dereferencer.dereference(body))

    dereferenceLambda(arguments, transpiledBody)
  }

  def dereferenceLambda(arguments: Seq[Variable], body: Expression): ExpressionOperation = {
    functionOperation(FunctionReference.Anonymous, arguments, body)
  }
}
