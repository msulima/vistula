package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast

object FunctionDefinitionHelper {

  val const = Identifier(observable = false)
  val obs = Identifier(observable = true)

  val defaults: Seq[(Ast.identifier, FunctionDefinition)] = Seq()

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    FunctionDefinition((1 to argumentsCount).map(_ => argumentDefinition), resultIsObservable = resultIsObservable)
  }

  def adaptArguments(argumentsCount: Int, argumentsAreObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    (1 to argumentsCount).map(_ => argumentDefinition)
  }

  def constDef(name: String, arguments: Identifier*) = {
    Ast.identifier(name) -> FunctionDefinition(arguments, resultIsObservable = false)
  }

  def obsDef(name: String, arguments: Identifier*) = {
    Ast.identifier(name) -> FunctionDefinition(arguments, resultIsObservable = true)
  }
}
