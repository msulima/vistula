package pl.msulima.vistula.transpiler.scope

sealed trait Type

case class Identifier(/*name: String,*/ observable: Boolean) extends Type

case class ArgumentDefinition(observable: Boolean)

case class FunctionDefinition(arguments: Seq[ArgumentDefinition], resultIsObservable: Boolean, varargs: Boolean = false) extends Type
