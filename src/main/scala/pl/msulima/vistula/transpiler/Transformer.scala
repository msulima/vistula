package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope._


object Transformer {

  def transform(program: Seq[Ast.stmt]): Seq[Expression] = {
    transform(program.map(Tokenizer.applyStmt), Scope.Empty)
  }

  def transform(program: Seq[Token], scope: Scope): Seq[Expression] = {
    run(program, scope).program
  }

  def extractScope(program: Seq[Ast.stmt]): Scope = {
    run(program.map(Tokenizer.applyStmt), Scope.Empty).scope
  }

  private def run(program: Seq[Token], scope: Scope): ScopedResult = {
    program.foldLeft(ScopedResult(scope, Seq()))((acc, stmt) => {
      val result = ScopeRunner.run(acc.scope)(stmt)

      result.copy(program = acc.program ++ result.program)
    })
  }

  private def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    Tokenizer.applyStmt.andThen(ScopeRunner.run(scope))
  }
}
