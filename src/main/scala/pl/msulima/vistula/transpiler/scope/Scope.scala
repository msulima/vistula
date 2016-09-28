package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Constant, Expression, Token}


case class Variable(id: Ast.identifier, `type`: ScopeElement)

case class ScopedResult(scope: Scope, program: Seq[Expression])

case class ScopePart(variables: Map[Ast.identifier, ScopeElement],
                     functions: Map[Token, FunctionDefinition],
                     classes: Map[ClassReference, ClassDefinition])

case class Scope(private val imports: ScopePart, declarations: ScopePart) {

  private val variables = imports.variables ++ declarations.variables
  private val functions = imports.functions ++ declarations.functions
  private val classes = imports.classes ++ declarations.classes

  def findById(id: Ast.identifier): Option[ScopeElement] = {
    variables.get(id).orElse(functions.get(Constant(id.name)).map(ScopeElement.const))
  }

  def findClass(id: ClassReference): ClassDefinition = {
    classes(id)
  }

  def addToScope(variable: Variable): Scope = {
    variable.`type` match {
      case ScopeElement(false, definition: FunctionDefinition) =>
        copy(declarations = declarations.copy(functions = declarations.functions + (Constant(variable.id.name) -> definition)))
      case t: ScopeElement =>
        copy(declarations = declarations.copy(variables = declarations.variables + (variable.id -> t)))
    }
  }

  def addToScope(id: ClassReference, classDefinition: ClassDefinition): Scope = {
    copy(declarations = declarations.copy(classes = declarations.classes + (id -> classDefinition)))
  }
}

object Scope {

  val Empty = {
    Scope(
      imports = ScopePart(
        variables = Map(
          Ast.identifier("vistula") -> ScopeElement.const(ClassDefinitionHelper.Vistula),
          Ast.identifier("stdlib") -> ScopeElement.observable(ClassDefinitionHelper.Stdlib)
        ),
        functions = Map(),
        classes = ClassDefinitionHelper.defaults
      ),
      declarations = ScopePart(Map(), Map(), Map())
    )
  }
}
