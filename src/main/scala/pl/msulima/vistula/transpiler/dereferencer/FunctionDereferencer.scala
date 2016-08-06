package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall
import pl.msulima.vistula.transpiler.scope.{ClassDefinition, FunctionDefinition, FunctionDefinitionHelper, ScopeElement}

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(FunctionDef, program, _, _) =>
      val argumentIds = program.drop(2)
      val funcDefinition = FunctionDefinitionHelper.adapt(argumentIds.size,
        argumentsAreObservable = true, resultIsObservable = true)

      ExpressionOperation(FunctionDef, program.map(dereference), ScopeElement(observable = false, funcDefinition))
    case operation@Operation(FunctionScope, program, _, _) =>
      val result = Transformer.scoped(program, scope)

      ExpressionOperation(FunctionScope, result.init :+ Return(result.last), result.last.`type`)
    case Operation(FunctionCall, arguments, func, _) =>
      val function = dereference(func)

      val funcDefinition = getDefinition(function, arguments)

      val body = ExpressionOperation(FunctionCall, function +: handleArguments(funcDefinition, arguments),
        ScopeElement(funcDefinition.resultIsObservable))

      // hacky
      if (function.`type`.observable && function.isInstanceOf[ExpressionOperation]) {
        ExpressionOperation(ExpressionFlatMap(body), Seq(function), function.`type`)
      } else {
        body
      }
  }

  private def getDefinition(function: Expression, arguments: Seq[Token]) = {
    function.`type`.`type` match {
      case definition: FunctionDefinition =>
        definition
      case _: ClassDefinition =>
        FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = function.`type`.observable,
          resultIsObservable = function.`type`.observable)
    }
  }

  private def handleArguments(definition: FunctionDefinition, arguments: Seq[Token]) = {
    val args = if (definition.varargs) {
      FunctionDefinitionHelper.adaptArguments(arguments.size, definition.arguments.head.observable)
    } else {
      require(arguments.size == definition.arguments.size,
        s"Wrong number of arguments: given ${arguments.size} expected ${definition.arguments.size}")
      definition.arguments
    }

    arguments.zip(args).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          Box(arg)
        } else {
          arg
        }
    }).map(dereference)
  }
}
