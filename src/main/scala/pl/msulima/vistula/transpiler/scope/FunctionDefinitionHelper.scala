package pl.msulima.vistula.transpiler.scope

object FunctionDefinitionHelper {

  val const = ScopeElement(observable = false, ClassReference.Object)
  val obs = ScopeElement(observable = true, ClassReference.Object)

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    val resultType = ScopeElement(resultIsObservable, ClassReference.Object)

    FunctionDefinition((1 to argumentsCount).map(_ => argumentDefinition), resultType = resultType)
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
