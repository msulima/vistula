package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast

sealed trait ScopeElement

case object NotExpression extends ScopeElement

case class Identifier(observable: Boolean, `type`: Ast.identifier = ClassDefinition.Object) extends ScopeElement {

  override def toString: String = {
    if (observable) {
      s"Obs<${`type`.name}>"
    } else {
      s"Val<${`type`.name}>"
    }
  }
}

case class FunctionDefinition(arguments: Seq[Identifier], resultIsObservable: Boolean, varargs: Boolean = false) extends ScopeElement {

  override def toString: String = {
    val args = if (varargs) {
      s"${boolToObs(arguments.head.observable)}: ${arguments.head.`type`.name} ..."
    } else {
      arguments.map(arg => {
        s"${boolToObs(arg.observable)}: ${arg.`type`.name}"
      }).mkString(", ")
    }
    val result = boolToObs(resultIsObservable)

    s"FunctionDefinition(($args) => $result)"
  }

  private def boolToObs(observable: Boolean) = {
    if (observable) {
      "obs"
    } else {
      "const"
    }
  }
}

case class ClassDefinition(fields: Map[Ast.identifier, ScopeElement]) extends ScopeElement

object ClassDefinition {

  val Object = Ast.identifier("vistula.lang.Object")
}
