package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionScope, Return}
import pl.msulima.vistula.transpiler.scope.{FunctionDefinitionHelper, ScopeElement}

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(FunctionDef, program) =>
      val argumentIds = program.drop(2)
      val funcDefinition = FunctionDefinitionHelper.adapt(argumentIds.size,
        argumentsAreObservable = true, resultIsObservable = true)

      ExpressionOperation(FunctionDef, program.map(dereference), ScopeElement(observable = false, funcDefinition))
    case operation@Operation(FunctionScope, program) =>
      val result = Transformer.scoped(program, scope)

      ExpressionOperation(FunctionScope, result.init :+ Return(result.last), result.last.`type`)
  }
}
