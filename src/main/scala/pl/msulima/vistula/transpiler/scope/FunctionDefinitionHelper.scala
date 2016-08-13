package pl.msulima.vistula.transpiler.scope

object FunctionDefinitionHelper {

  val const = ScopeElement(observable = false)
  val obs = ScopeElement(observable = true)

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    FunctionDefinition((1 to argumentsCount).map(_ => argumentDefinition), resultType = ScopeElement(resultIsObservable))
  }

  def adaptArguments(argumentsCount: Int, argumentsAreObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    (1 to argumentsCount).map(_ => argumentDefinition)
  }
}
