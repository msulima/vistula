package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall
import pl.msulima.vistula.transpiler.scope._

trait FunctionCallDereferencer {
  this: Dereferencer =>

  def functionCallDereferencer: PartialFunction[Token, Expression] = {
    case Operation(_: FunctionCall, func +: arguments) =>
      val function = dereferenceFunction(func, arguments)
      val funcDefinition = getDefinition(function, arguments)
      val (observables, inputs) = findSubstitutes(function, funcDefinition, arguments)

      val body = ExpressionOperation(FunctionCall(funcDefinition.constructor), inputs, funcDefinition.resultType)

      if (observables.isEmpty) {
        body
      } else if (body.`type`.observable) {
        ExpressionOperation(RxFlatMap(body), Seq(function), body.`type`)
      } else {
        ExpressionOperation(RxMap(body), observables, body.`type`)
      }
  }

  private def dereferenceFunction(function: Token, arguments: Seq[Token]) = {
    dereference(function) match {
      case ExpressionConstant(value, ScopeElement(true, _: ClassReference)) =>
        val definition = FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = true,
          resultIsObservable = true)
        ExpressionConstant(value, ScopeElement(observable = false, definition))
      case func =>
        func
    }
  }

  private def getDefinition(function: Expression, arguments: Seq[Token]) = {
    function.`type`.`type` match {
      case definition: FunctionDefinition =>
        definition
      case _: ClassReference =>
        FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = function.`type`.observable,
          resultIsObservable = function.`type`.observable)
    }
  }

  private def findSubstitutes(function: Expression, funcDefinition: FunctionDefinition, arguments: Seq[Token]): (Seq[Expression], Seq[Expression]) = {
    val functionSubstitutes = OperationDereferencer.extractObservables(function)
    val argumentsSubstitutes = arguments.zip(funcDefinition.adapt(arguments)).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          Seq() -> dereference(Box(arg))
        } else {
          OperationDereferencer.extractObservables(dereference(arg))
        }
    })

    val observables = functionSubstitutes._1 ++ argumentsSubstitutes.flatMap(_._1)
    val inputs = functionSubstitutes._2 +: argumentsSubstitutes.map(_._2)

    (observables, inputs)
  }
}
