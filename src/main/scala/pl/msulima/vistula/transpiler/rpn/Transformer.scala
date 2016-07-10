package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression.{Assign, Return}
import pl.msulima.vistula.transpiler.{Scope, rpn}

case class ScopedResult(scope: Scope, program: Seq[Token])

object Transformer {

  def returnLast(program: Seq[Ast.stmt]): Token = {
    val result = scoped(program)

    if (result.isEmpty || result.size == 1) {
      checkObservable(result.last, result)
    } else {
      val body = result.init :+ Operation(Return, Seq(result.last), Constant("ignore"))

      checkObservable(result.last, body)
    }
  }

  private def checkObservable(token: Token, body: Seq[Token]) = {
    val useFlatMap = token.isInstanceOf[Observable]
    val operation = Operation(Wrap, body, Constant("ignore"))

    if (useFlatMap) {
      Observable(operation)
    } else {
      operation
    }
  }

  def scoped(program: Seq[Ast.stmt]): Seq[Token] = {
    program.foldLeft(ScopedResult(pl.msulima.vistula.transpiler.Transpiler.EmptyScope, Seq()))((acc, stmt) => {
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
    token match {
      case Operation(Assign(identifier, mutable), _, _) =>
        val nextScope = if (mutable) {
          currentScope.copy(observables = currentScope.observables :+ identifier)
        } else {
          currentScope.copy(variables = currentScope.variables :+ identifier)
        }

        ScopedResult(nextScope, Seq(token))
      case _ =>
        ScopedResult(currentScope, Seq(token))
    }
  }
}
