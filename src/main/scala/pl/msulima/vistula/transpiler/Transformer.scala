package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl
import pl.msulima.vistula.transpiler.scope._

import scala.annotation.tailrec


object Transformer {

  def transform(program: Seq[Ast.stmt]): Seq[Expression] = {
    scoped(program.map(Tokenizer.applyStmt), Scope.Empty)
  }

  def scoped(program: Seq[Token], scope: Scope): Seq[Expression] = {
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

  @tailrec
  private def run(scope: Scope)(token: Token): ScopedResult = {
    token match {
      case Introduce(variable, body) =>
        val ns = scope.addToScope(variable)
        run(ns)(body)
      case IntroduceClass(id, definition, constructor) =>
        val ns = scope.addToScope(id, definition)
        run(ns)(constructor)
      case _ =>
        ScopedResult(scope, Seq(DereferencerImpl(scope, token)))
    }
  }
}
