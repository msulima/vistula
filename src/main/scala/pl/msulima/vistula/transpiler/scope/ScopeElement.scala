package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast


case class ScopeElement(observable: Boolean, `type`: ClassType) {

  override def toString: String = {
    if (observable) {
      s"Obs<${`type`}>"
    } else {
      s"Val<${`type`}>"
    }
  }
}

object ScopeElement {

  val Default = observable(ClassReference.Object)
  val DefaultConst = const(ClassReference.Object)

  def const(`type`: ClassType) = ScopeElement(observable = false, `type`)

  def observable(`type`: ClassType) = ScopeElement(observable = true, `type`)
}

sealed trait ClassType

case class FunctionDefinition(arguments: Seq[ScopeElement], resultType: ScopeElement,
                              varargs: Boolean = false, constructor: Boolean = false) extends ClassType {

  def adapt(arguments: Seq[_]): Seq[ScopeElement] = {
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

    s"FunctionDefinition(($args) => $resultType)"
  }

  private def boolToObs(observable: Boolean) = {
    if (observable) {
      "obs"
    } else {
      "const"
    }
  }
}

case class FunctionReference(`package`: Package, name: Ast.identifier) {

  def toIdentifier = Ast.identifier((`package`.path :+ name).map(_.name).mkString("."))
}

object FunctionReference {

  val Anonymous = FunctionReference(Package.Root, Ast.identifier(""))
}

case class ClassDefinition(fields: Map[Ast.identifier, ScopeElement])

case class ClassReference(`package`: Package, name: Ast.identifier) extends ClassType {

  def toIdentifier = Ast.identifier((`package`.path :+ name).map(_.name).mkString("."))

  override def toString: String = s"ClassReference(${toIdentifier.name})"
}

object ClassReference {

  val Object = ClassReference("vistula.lang.Object")

  def apply(path: String): ClassReference = {
    ClassReference(path.split("\\.").map(Ast.identifier))
  }

  def apply(typedef: Seq[Ast.identifier]): ClassReference = {
    if (typedef.isEmpty) {
      Object
    } else {
      ClassReference(Package(typedef.init), typedef.last)
    }
  }
}
