package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl
import pl.msulima.vistula.transpiler.expression.control.FunctionDef

case object ScopeRunner {

  def run(scope: Scope)(token: Token): ScopedResult = {
    val dereferencer = new DereferencerImpl(scope)

    token match {
      case Introduce(variable, body) =>
        val result = DereferencerImpl(scope, token)
        val ns = scope.addToScope(inferType(variable, result))
        ScopedResult(ns, Seq(result))
      case ImportVariable(variable) =>
        val ns = scope.addToScope(variable)
        ScopedResult(ns, Seq())
      case Direct(stmt: Ast.stmt.Import) =>
        dereferencer.importDereferencer(stmt)
      case Direct(classDef: Ast.stmt.ClassDef) =>
        val (classDefinition, constructor, methods) = dereferencer.classDereferencer(classDef)
        val classReference = ClassReference(Seq(classDef.name))
        val ns = scope.addToScope(classReference, classDefinition)

        val scopedResult = run(ns)(constructor)
        scopedResult.copy(program = scopedResult.program ++ methods)
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
