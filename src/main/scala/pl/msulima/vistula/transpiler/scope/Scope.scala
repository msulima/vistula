package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Constant, Token}


case class Variable(id: Ast.identifier, `type`: Type)

case class ScopedResult(scope: Scope, program: Seq[Token])

case class Scope(variables: Map[Ast.identifier, Identifier], functions: Map[Token, FunctionDefinition]) {

  def isKnownStatic(id: Ast.identifier) = {
    variables.get(id).exists(!_.observable) || functions.contains(Constant(id.name))
  }

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

  val Empty = {
    Scope(variables = Map(), functions = FunctionDefinitionHelper.defaults.map({
      case (Ast.identifier(name), definition) =>
        Constant(name) -> definition
    }).toMap)
  }
}
