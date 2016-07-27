package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

sealed trait Type

case class Identifier(/*name: String,*/ observable: Boolean) extends Type

case class ArgumentDefinition(observable: Boolean)

case class FunctionDefinition(arguments: Seq[ArgumentDefinition], resultIsObservable: Boolean, varargs: Boolean = false) extends Type

case class Variable(id: Ast.identifier, `type`: Type)

case class ScopedResult(scope: Scope, program: Seq[Token])

case class Scope(variables: Map[Ast.identifier, Identifier], functions: Map[Token, FunctionDefinition]) {

  def addToScope(variable: Variable) = {
    variable.`type` match {
      case t: Identifier =>
        copy(variables = variables + (variable.id -> t))
      case t: FunctionDefinition =>
        copy(functions = functions + (Constant(variable.id.name) -> t))
    }
  }
}

object Scope {

  import FunctionDefinition._

  private val definitions: Seq[(Ast.identifier, FunctionDefinition)] = Seq(
    obsDef("vistula.ifStatement", obs, obs, obs),
    obsDef("vistula.dom.textObservable", obs),
    obsDef("vistula.dom.textNode", const),
    obsDef("vistula.zipAndFlatten", const),
    obsDef("vistula.aggregate", obs, obs, const),
    obsDef("vistula.dom.createBoundElement", const, const, const, const),
    obsDef("vistula.dom.createElement", const, const, const),
    obsDef("vistula.ifChangedArrays", obs, const, const),
    obsDef("vistula.wrap", const),
    Ast.identifier("vistula.Seq.apply") -> FunctionDefinition(Seq(obs), resultIsObservable = true, varargs = true)
  )

  val Empty = {
    Scope(variables = Map(), functions = definitions.map({
      case (Ast.identifier(name), definition) =>
        Constant(name) -> definition
    }).toMap)
  }
}

object FunctionDefinition {

  val const = ArgumentDefinition(observable = false)
  val obs = ArgumentDefinition(observable = true)

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    FunctionDefinition((1 to argumentsCount).map(_ => argumentDefinition), resultIsObservable = resultIsObservable)
  }

  def adaptArguments(argumentsCount: Int, argumentsAreObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    (1 to argumentsCount).map(_ => argumentDefinition)
  }

  def constDef(name: String, arguments: ArgumentDefinition*) = {
    Ast.identifier(name) -> FunctionDefinition(arguments, resultIsObservable = false)
  }

  def obsDef(name: String, arguments: ArgumentDefinition*) = {
    Ast.identifier(name) -> FunctionDefinition(arguments, resultIsObservable = true)
  }
}

