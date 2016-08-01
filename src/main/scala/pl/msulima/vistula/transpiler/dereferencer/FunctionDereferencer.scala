package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, FunctionDefinitionHelper, Identifier}

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer2: PartialFunction[Token, Expression] = {
    case operation@Operation(FunctionScope, program, _, _) =>
      val result = Transformer.scoped(program, scope)

      ExpressionOperation(FunctionScope, result.init :+ Return(result.last), result.last.`type`)
    case Operation(FunctionCall, arguments, func, _) =>
      val dereferencedFunc = dereference2(func)

      val funcDefinition = dereferencedFunc match {
        case ExpressionOperation(Reference, _, definition: FunctionDefinition) =>
          definition
        case ExpressionConstant(value, definition: FunctionDefinition) =>
          definition
        case ExpressionConstant(value, id: Identifier) =>
          FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = id.observable, resultIsObservable = id.observable)
      }

      ExpressionOperation(FunctionCall, dereferencedFunc +: handleArguments2(funcDefinition, arguments), funcDefinition)
  }

  private def handleArguments2(definition: FunctionDefinition, arguments: Seq[Token]) = {
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
          dereference2(Box(arg))
        } else {
          dereference2(arg)
        }
    })
  }
}
