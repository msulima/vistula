package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl
import pl.msulima.vistula.transpiler.dereferencer.control.FunctionDef

case object ScopeRunner {

  def run(scope: Scope, `package`: Package)(token: Token): ScopedResult = {
    val dereferencer = DereferencerImpl(scope, `package`)

    token match {
      case Direct(Ast.stmt.DeclareStmt(identifier, stmt, mutable, typedef)) =>
        dereferencer.dereferenceAndIntroduce(identifier, stmt, mutable, typedef)
      case Introduce(variable, body) =>
        dereferencer.dereferenceIntroduce(variable, dereferencer.dereference(body))
      case ImportVariable(variable) =>
        val ns = scope.addToScope(variable)
        ScopedResult(ns, Seq())
      case Direct(Ast.stmt.Pass) =>
        ScopedResult(scope, Seq())
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
