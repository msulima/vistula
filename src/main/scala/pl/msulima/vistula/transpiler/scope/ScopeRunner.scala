package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl
import pl.msulima.vistula.transpiler.expression.control.FunctionDef

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
      case introduce@IntroduceClass(id, fields, methods, constructor) =>
        val ns = scope.addToScope(id, new DereferencerImpl(scope).classDereferencer(introduce))

        run(ns)(constructor)
      case op@Operation(func@FunctionDef(id, _, _), _) =>
        val body = DereferencerImpl(scope, op)
        val ns = scope.addToScope(Variable(id, body.`type`))
        ScopedResult(ns, Seq(body))
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
