package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Constant, Expression, Token}


case class Variable(id: Ast.identifier, `type`: ScopeElement)

case class ScopedResult(scope: Scope, program: Seq[Expression])

case class Scope(variables: Map[Ast.identifier, ScopeElement], functions: Map[Token, FunctionDefinition],
                 classes: Map[Ast.identifier, ClassDefinition]) {

  def findById(id: Ast.identifier): Option[ScopeElement] = {
    variables.get(id).orElse(functions.get(Constant(id.name)).map(fn => {
      ScopeElement(observable = false, fn)
    }))
  }

  def isKnownStatic(id: Ast.identifier) = {
    variables.get(id).exists(!_.observable) || functions.contains(Constant(id.name))
  }

  def addToScope(variable: Variable) = {
    variable.`type` match {
      case ScopeElement(false, definition: FunctionDefinition) =>
        copy(functions = functions + (Constant(variable.id.name) -> definition))
      case t: ScopeElement =>
        copy(variables = variables + (variable.id -> t))
    }
  }
}

object Scope {

  val Empty = {
    Scope(
      variables = Map(
        Ast.identifier("vistula") -> ScopeElement(observable = false, `type` = ClassDefinitionHelper.Vistula)
      ),
      functions = Map(),
      classes = ClassDefinitionHelper.defaults.map({
        case (name, definition) =>
          name -> definition
      }).toMap)
  }
}
