package pl.msulima.vistula.transpiler

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope._


object Transformer {

  def transform(program: Seq[Ast.stmt], `package`: Package, predef: ScopePart = Scope.EmptyScopePart): Seq[Expression] = {
    val mergeImport = Scope.Empty.mergeImport(predef)

    transform(program.map(Direct.apply), mergeImport, `package`)
  }

  def transform(program: Seq[Token], scope: Scope, `package`: Package): Seq[Expression] = {
    run(program, scope, `package`).program
  }

  def extractScope(program: Seq[Ast.stmt], `package`: Package): Scope = {
    run(program.map(Direct.apply), Scope.Empty, `package`).scope
  }

  private def run(program: Seq[Token], scope: Scope, `package`: Package): ScopedResult = {
    program.foldLeft(ScopedResult(scope, Seq()))((acc, stmt) => {
      val result = ScopeRunner.run(acc.scope, `package`)(stmt)

      result.copy(program = acc.program ++ result.program)
    })
  }
}
