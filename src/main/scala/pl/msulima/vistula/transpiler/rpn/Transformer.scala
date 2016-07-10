package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression.Assign
import pl.msulima.vistula.transpiler.{Scope, rpn}

case class ScopedResult(scope: Scope, program: Seq[Token])

object Transformer {

  def scoped(program: Seq[Ast.stmt]): ScopedResult = {
    program.foldLeft(ScopedResult(pl.msulima.vistula.transpiler.Transpiler.EmptyScope, Seq()))((acc, stmt) => {
      val result = apply(acc.scope)(stmt)

      result.copy(program = acc.program ++ result.program)
    })
  }

  private def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    rpn.Tokenizer.applyStmt.andThen(Dereferencer.apply(scope)).andThen(extractScope(scope))
  }

  def applyExpr(scope: Scope): PartialFunction[Ast.expr, ScopedResult] = {
    rpn.Tokenizer.apply.andThen(Dereferencer.apply(scope)).andThen(extractScope(scope))
  }

  private def extractScope(currentScope: Scope)(token: Token) = {
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
