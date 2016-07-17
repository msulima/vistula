package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression.control.Return
import pl.msulima.vistula.transpiler.rpn.expression.reference.Declare
import pl.msulima.vistula.transpiler.{Scope, ScopedResult, rpn}


object Transformer {

  private val EmptyScope = Scope(Seq(), Seq())

  def wrapAndReturnLast(program: Seq[Ast.stmt]): Token = {
    val result = scoped(program)

    val body = if (result.isEmpty || result.size == 1) {
      result
    } else {
      result.init :+ Operation(Return, Seq(result.last), Tokenizer.Ignored)
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

  def returnLast(program: Seq[Ast.stmt]): Token = {
    val result = scoped(program)

    val toReturn = Operation(Return, Seq(result.last), Tokenizer.Ignored)

    Operation(FunctionScope, result.init :+ toReturn, Tokenizer.Ignored)
  }

  def scoped(program: Seq[Ast.stmt]): Seq[Token] = {
    program.foldLeft(ScopedResult(EmptyScope, Seq()))((acc, stmt) => {
      val result = apply(acc.scope)(stmt)

      result.copy(program = acc.program ++ result.program)
    }).program
  }

  private def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    rpn.Tokenizer.applyStmt.andThen(run(scope))
  }

  def applyExpr(scope: Scope): PartialFunction[Ast.expr, ScopedResult] = {
    rpn.Tokenizer.apply.andThen(run(scope))
  }

  private def run(scope: Scope)(token: Token): ScopedResult = {
    extractScope(scope, Dereferencer(scope, token))
  }

  private def extractScope(currentScope: Scope, token: Token) = {
    val nextScope = token match {
      case Operation(Declare(identifier, mutable), _, _) =>
        if (mutable) {
          currentScope.copy(observables = currentScope.observables :+ identifier)
        } else {
          currentScope.copy(variables = currentScope.variables :+ identifier)
        }
      case _ =>
        currentScope
    }

    ScopedResult(nextScope, Seq(token))
  }
}
