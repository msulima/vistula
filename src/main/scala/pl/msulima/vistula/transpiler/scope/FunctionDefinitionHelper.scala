package pl.msulima.vistula.transpiler.scope

object FunctionDefinitionHelper {

  val const = ScopeElement.DefaultConst
  val obs = ScopeElement.Default

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = boolToObs(argumentsAreObservable)
    val resultType = ScopeElement(resultIsObservable, ClassReference.Object)

    FunctionDefinition((1 to argumentsCount).map(_ => argumentDefinition), resultType = resultType)
  }

  def adaptArguments(argumentsCount: Int, argumentsAreObservable: Boolean) = {
    val argumentDefinition = boolToObs(argumentsAreObservable)
    (1 to argumentsCount).map(_ => argumentDefinition)
  }

  private def boolToObs(argumentsAreObservable: Boolean): ScopeElement = {
    if (argumentsAreObservable) {
      obs
    } else {
      const
    }
  }
}
