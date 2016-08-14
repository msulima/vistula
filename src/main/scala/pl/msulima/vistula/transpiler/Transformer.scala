package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope._


object Transformer {

  def transform(program: Seq[Ast.stmt]): Seq[Expression] = {
    scoped(program.map(Tokenizer.applyStmt), Scope.Empty)
  }

  def scoped(program: Seq[Token], scope: Scope): Seq[Expression] = {
    program.foldLeft(ScopedResult(scope, Seq()))((acc, stmt) => {
      val result = ScopeRunner.run(acc.scope)(stmt)

      result.copy(program = acc.program ++ result.program)
    }).program
  }

  private def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    Tokenizer.applyStmt.andThen(ScopeRunner.run(scope))
  }
}
