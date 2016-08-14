package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl

import scala.annotation.tailrec

case object ScopeRunner {

  @tailrec
  def run(scope: Scope)(token: Token): ScopedResult = {
    token match {
      case Introduce(variable, body) =>
        val result = DereferencerImpl(scope, token)
        val ns = scope.addToScope(inferType(variable, result))
        ScopedResult(ns, Seq(result))
      case Import(variable) =>
        val ns = scope.addToScope(variable)
        ScopedResult(ns, Seq())
      case IntroduceClass(id, definition, constructor) =>
        val ns = scope.addToScope(id, definition)
        run(ns)(constructor)
      case _ =>
        ScopedResult(scope, Seq(DereferencerImpl(scope, token)))
    }
  }

  private def inferType(variable: Variable, result: Expression): Variable = {
    if (variable.`type`.`type` == ClassReference.Object) {
      variable.copy(`type` = variable.`type`.copy(`type` = result.`type`.`type`))
    } else {
      variable
    }
  }
}
