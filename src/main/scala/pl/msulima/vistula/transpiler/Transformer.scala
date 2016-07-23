package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.expression.control.Return


object Transformer {

  private val EmptyScope = Scope(Seq())

  def wrapAndReturnLast(program: Seq[Ast.stmt]): Token = {
    val result = transform(program)

    val body = if (result.isEmpty || result.size == 1) {
      result
    } else {
      result.init :+ Return(result.last)
    }

    checkObservable(result.last, body)
  }

  private def checkObservable(token: Token, body: Seq[Token]) = {
    val isObservable = token.isInstanceOf[Observable]
    val operation = Operation(WrapScope, body, Tokenizer.Ignored)

    if (isObservable) {
      Observable(operation)
    } else {
      operation
    }
  }

  def transform(program: Seq[Ast.stmt]): Seq[Token] = {
    scoped(program.map(Tokenizer.applyStmt), EmptyScope)
  }

  def scoped(program: Seq[Token], scope: Scope): Seq[Token] = {
    program.foldLeft(ScopedResult(scope, Seq()))((acc, stmt) => {
      val result = run(acc.scope)(stmt)

      result.copy(program = acc.program ++ result.program)
    }).program
  }

  private def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    Tokenizer.applyStmt.andThen(run(scope))
  }

  def applyExpr(scope: Scope): PartialFunction[Ast.expr, ScopedResult] = {
    Tokenizer.apply.andThen(run(scope))
  }

  private def run(scope: Scope)(token: Token): ScopedResult = {
    token match {
      case Introduce(variable, body) =>
        val ns = scope.copy(variables = scope.variables :+ variable)
        ScopedResult(ns, Seq(Dereferencer(ns, body)))
      case _ =>
        ScopedResult(scope, Seq(Dereferencer(scope, token)))
    }
  }
}
