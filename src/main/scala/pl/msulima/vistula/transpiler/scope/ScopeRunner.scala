package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl
import pl.msulima.vistula.transpiler.expression.control.FunctionDef

case object ScopeRunner {

  def run(scope: Scope, `package`: Package)(token: Token): ScopedResult = {
    val dereferencer = DereferencerImpl(scope, `package`)

    token match {
      case Introduce(variable, body) =>
        dereferencer.dereferenceIntroduce(variable, dereferencer.dereference(body))
      case ImportVariable(variable) =>
        val ns = scope.addToScope(variable)
        ScopedResult(ns, Seq())
      case Direct(stmt: Ast.stmt.Import) =>
        dereferencer.importDereferencer(stmt)
      case Direct(classDef: Ast.stmt.ClassDef) =>
        dereferencer.classDereferencer(classDef)
      case op@Direct(func: Ast.stmt.FunctionDef) =>
        dereferencer.dereferenceAndAddToScope(func)
      case op@Operation(func@FunctionDef(id, _, _), _) =>
        val body = dereferencer.dereference(op)
        val ns = scope.addToScope(Variable(id.name, body.`type`))
        ScopedResult(ns, Seq(body))
      case _ =>
        ScopedResult(scope, Seq(dereferencer.dereference(token)))
    }
  }
}
