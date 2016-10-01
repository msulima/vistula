package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler.expression.data.StaticDict
import pl.msulima.vistula.transpiler.expression.reference.Declare
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{Constant, Operation}
import pl.msulima.vistula.{Package, Vistula}

object ImportDereferencer {

  private val EmptyDict = Operation(StaticDict, Seq())

  def packagePreambule(`package`: Package) = {
    if (`package` == Package.Root) {
      Seq()
    } else {
      val root = Declare(`package`.path.head, EmptyDict, mutable = false, declare = true)
      val modules = `package`.path.inits.toSeq.reverse.drop(2).map(path => {
        Declare(Package(path).toIdentifier, EmptyDict, mutable = false, declare = false)
      })
      root +: modules
    }
  }
}

trait ImportDereferencer {
  this: Dereferencer =>

  def importDereferencer(`import`: Ast.stmt.Import) = `import` match {
    case Ast.stmt.Import(Ast.alias(identifier, None) +: _) =>
      ScopedResult(dereferenceImport(identifier), Seq())
  }

  private def dereferenceImport(identifier: identifier): Scope = {
    val classReference = ClassReference(identifier.name)
    val declarations = Vistula.loadFile(classReference).declarations

    val intermediatePackageObjects = classReference.`package`.parents.map(path => {
      val parentReference = path.parent.packageObjectReference
      val nestedReference = path.packageObjectReference

      // FIXME should merge with other modules
      ClassReferenceAndDefinition(
        parentReference, ClassDefinition(Map(path.path.last -> ScopeElement.const(nestedReference)))
      )
    })

    scope.addToScope(getTopLevelPackageObject(classReference))
      .addToScope(intermediatePackageObjects)
      .addToScope(getPackageObject(classReference, declarations))
      .addToScope(declarations)
  }

  private def getTopLevelPackageObject(classReference: ClassReference): Variable = {
    val topLevelPackage = classReference.`package`.parents.head.parent

    Variable(topLevelPackage.toIdentifier, ScopeElement.const(topLevelPackage.packageObjectReference))
  }

  private def getPackageObject(classReference: ClassReference, declarations: ScopePart) = {
    val packageObjectDefinition = ClassDefinition(declarations.functions.collect({
      case (Constant(id), func) if func.constructor =>
        (Ast.identifier(id), ScopeElement.const(func))
      case (Constant(id), func) =>
        (Ast.identifier(id), ScopeElement.const(func))
    }))

    ClassReferenceAndDefinition(classReference.`package`.packageObjectReference, packageObjectDefinition)
  }
}
