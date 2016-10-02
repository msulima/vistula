package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall
import pl.msulima.vistula.transpiler.scope._

trait FunctionCallDereferencer {
  this: Dereferencer =>

  def functionCallDereferencer: PartialFunction[Token, Expression] = {
    case Operation(_: FunctionCall, func +: args) =>
      val function = dereferenceFunction(func, args)
      val funcDefinition = getDefinition(function, args)
      val arguments = dereferenceArguments(funcDefinition, args)

      functionCall(function, funcDefinition, arguments)
  }

  def functionCall(func: Token, arguments: Seq[Expression]): ExpressionOperation = {
    val function = dereferenceFunction(func, arguments)
    val funcDefinition = getDefinition(function, arguments)

    functionCall(function, funcDefinition, arguments)
  }

  private def dereferenceFunction(function: Token, arguments: Seq[_]) = {
    dereference(function) match {
      case ExpressionConstant(value, ScopeElement(true, _: ClassReference)) =>
        val definition = FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = true,
          resultIsObservable = true)
        ExpressionConstant(value, ScopeElement.const(definition))
      case func =>
        func
    }
  }

  private def getDefinition(function: Expression, arguments: Seq[_]) = {
    function.`type`.`type` match {
      case definition: FunctionDefinition =>
        definition
      case _: ClassReference =>
        FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = function.`type`.observable,
          resultIsObservable = function.`type`.observable)
    }
  }

  private def dereferenceArguments(funcDefinition: FunctionDefinition, arguments: Seq[Token]) = {
    arguments.zip(funcDefinition.adapt(arguments)).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          Box(arg)
        } else {
          arg
        }
    }).map(dereference)
  }

  private def functionCall(function: Expression, funcDefinition: FunctionDefinition, arguments: Seq[Expression]): ExpressionOperation = {
    val (observables, inputs) = findSubstitutes(function, funcDefinition, arguments)
    val body = ExpressionOperation(FunctionCall(funcDefinition.constructor), inputs, funcDefinition.resultType)

    RxMapOp(observables, body)
  }

  private def findSubstitutes(function: Expression, funcDefinition: FunctionDefinition, arguments: Seq[Expression]): (Seq[Expression], Seq[Expression]) = {
    val functionSubstitutes = OperationDereferencer.extractObservables(function)
    val argumentsSubstitutes = arguments.zip(funcDefinition.adapt(arguments)).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          Seq() -> arg
        } else {
          OperationDereferencer.extractObservables(arg)
        }
    })

    val observables = functionSubstitutes._1 ++ argumentsSubstitutes.flatMap(_._1)
    val inputs = functionSubstitutes._2 +: argumentsSubstitutes.map(_._2)

    (observables, inputs)
  }
}
