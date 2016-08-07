package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.Token


case class ScopeElement(observable: Boolean, `type`: ClassType = ClassDefinition.Object) {

  override def toString: String = {
    if (observable) {
      s"Obs<${`type`}>"
    } else {
      s"Val<${`type`}>"
    }
  }
}

sealed trait ClassType

case class FunctionDefinition(arguments: Seq[ScopeElement], resultIsObservable: Boolean, varargs: Boolean = false) extends ClassType {

  def adapt(arguments: Seq[Token]): Seq[ScopeElement] = {
    if (varargs) {
      FunctionDefinitionHelper.adaptArguments(arguments.size, this.arguments.head.observable)
    } else {
      require(arguments.size == this.arguments.size,
        s"Wrong number of arguments: given ${arguments.size} expected ${this.arguments.size}")
      this.arguments
    }
  }

  override def toString: String = {
    val args = if (varargs) {
      s"${boolToObs(arguments.head.observable)}: ${arguments.head.`type`} ..."
    } else {
      arguments.map(arg => {
        s"${boolToObs(arg.observable)}: ${arg.`type`}"
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

case class ClassDefinition(name: Ast.identifier, fields: Map[Ast.identifier, ScopeElement]) extends ClassType {

  override def toString: String = {
    name.name
  }
}

object ClassDefinition {

  val Object = ClassDefinition(Ast.identifier("vistula.lang.Object"), Map())
}
