package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Constant, Expression, Token}


case class Variable(id: Ast.identifier, `type`: ScopeElement)

case class ScopedResult(scope: Scope, program: Seq[Expression])

case class ScopePart(variables: Map[Ast.identifier, ScopeElement],
                     functions: Map[Token, FunctionDefinition],
                     classes: Map[ClassReference, ClassDefinition]) {

  def addToScope(other: ScopePart): ScopePart = {
    copy(
      functions = functions ++ other.functions,
      classes = classes ++ other.classes
    )
  }

  def mergeIntoScope(definition: ClassReferenceAndDefinition): ScopePart = {
    val mergedClassDefinition = classes.get(definition.reference) match {
      case Some(existingDefinition) =>
        existingDefinition.copy(fields = existingDefinition.fields ++ definition.definition.fields)
      case None =>
        definition.definition
    }

    copy(classes = classes + (definition.reference -> mergedClassDefinition))
  }
}

case class Scope(private val imports: ScopePart, declarations: ScopePart) {

  private val variables = imports.variables ++ declarations.variables
  private val functions = imports.functions ++ declarations.functions
  private val classes = imports.classes ++ declarations.classes

  def findById(id: Ast.identifier): Option[ScopeElement] = {
    variables.get(id).orElse(functions.get(Constant(id.name)).map(ScopeElement.const))
  }

  def findClass(id: ClassReference): ClassDefinition = {
    if (!classes.contains(id)) {
      println("asdf")
    }
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

  def mergeIntoScope(definitions: Seq[ClassReferenceAndDefinition]): Scope = {
    definitions.foldLeft(this)({
      case (acc, variable) => acc.mergeIntoScope(variable)
    })
  }

  def mergeIntoScope(definition: ClassReferenceAndDefinition): Scope = {
    copy(declarations = declarations.mergeIntoScope(definition))
  }

  def mergeImport(other: ScopePart): Scope = {
    copy(imports = imports.addToScope(other))
  }

  def addToScope(definition: ClassReferenceAndDefinition): Scope = {
    copy(declarations = declarations.copy(classes = declarations.classes + (definition.reference -> definition.definition)))
  }

  def addToScope(other: ScopePart): Scope = {
    copy(declarations = declarations.addToScope(other))
  }
}

object Scope {

  val EmptyScopePart = ScopePart(Map(), Map(), Map())

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
      declarations = EmptyScopePart
    )
  }
}
