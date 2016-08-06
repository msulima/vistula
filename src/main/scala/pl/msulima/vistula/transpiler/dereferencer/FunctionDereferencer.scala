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
      val function = dereferenceFunction(func, arguments)
      val funcDefinition = getDefinition(function, arguments)
      val (observables, inputs) = findSubstitutes(function, funcDefinition, arguments)

      val body = ExpressionOperation(FunctionCall, inputs, ScopeElement(funcDefinition.resultIsObservable))

      if (observables.isEmpty) {
        body
      } else if (function.`type`.observable) {
        ExpressionOperation(ExpressionFlatMap(body), Seq(function), function.`type`)
      } else {
        ExpressionOperation(ExpressionMap(body), observables, ScopeElement(observable = true))
      }
  }

  private def dereferenceFunction(function: Token, arguments: Seq[Token]) = {
    dereference(function) match {
      case ExpressionConstant(value, ScopeElement(true, clazz: ClassDefinition)) =>
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
      case _: ClassDefinition =>
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
