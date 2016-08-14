package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionScope, Return}
import pl.msulima.vistula.transpiler.scope.{FunctionDefinitionHelper, ScopeElement}

trait FunctionDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(FunctionDef, program) =>
      val argumentIds = program.drop(2)
      val funcDefinition = FunctionDefinitionHelper.adapt(argumentIds.size,
        argumentsAreObservable = true, resultIsObservable = true)

      ExpressionOperation(FunctionDef, program.map(dereference), ScopeElement(observable = false, funcDefinition))
    case operation@Operation(FunctionScope, program) =>
      val result = Transformer.scoped(program, scope)
      val maybeLast = findReturn(result)

      ExpressionOperation(FunctionScope, result.init ++ maybeLast.toSeq, result.last.`type`)
  }

  private def findReturn(result: Seq[Expression]): Option[ExpressionOperation] = {
    result.last match {
      case ExpressionOperation(Return, Nil, _) =>
        None
      case ExpressionOperation(Return, x :: Nil, _) =>
        val y = toObservable(x)
        Some(ExpressionOperation(Return, Seq(y), y.`type`))
      case x =>
        val y = toObservable(x)
        Some(ExpressionOperation(Return, Seq(y), y.`type`))
    }
  }
}
