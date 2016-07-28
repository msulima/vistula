package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast

sealed trait ScopeElement

case class Identifier(observable: Boolean, `type`: Ast.identifier = ClassDefinition.Object) extends ScopeElement

case class FunctionDefinition(arguments: Seq[Identifier], resultIsObservable: Boolean, varargs: Boolean = false) extends ScopeElement

case class ClassDefinition(fields: Map[Ast.identifier, ScopeElement]) extends ScopeElement

object ClassDefinition {

  val Object = Ast.identifier("Object") // Ast.identifier("vistula.lang.Object")
}
