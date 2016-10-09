package pl.msulima.vistula.transpiler.dereferencer.modules

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.data.StaticDict
import pl.msulima.vistula.transpiler.dereferencer.reference.Declare
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.{Package, Vistula}

object ImportDereferencer {

  def modulePreambule(`package`: Package): Seq[Expression] = {
    if (`package` == Package.Root) {
      Seq()
    } else {
      val id = `package`.path.head
      Seq(declareAsEmptyDict(id, declare = true))
    }
  }

  def packagePreambule(`package`: Package): Seq[Expression] = {
    if (`package`.path.size > 1) {
      Seq(declareAsEmptyDict(`package`.toIdentifier, declare = false))
    } else {
      Seq()
    }
  }

  private def declareAsEmptyDict(id: identifier, declare: Boolean): ExpressionOperation = {
    val identifier = ExpressionConstant(id.name, ScopeElement.DefaultConst)
    val body = ExpressionOperation(StaticDict, Seq(), ScopeElement.const(ClassReference.Object))

    ExpressionOperation(Declare(declare, mutable = false), Seq(identifier, body), ScopeElement.DefaultConst)
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

    val scopeWithTopPackages = if (classReference.`package`.parent == Package.Root) {
      scope
    } else {
      scope.addToScope(getTopLevelPackageObject(classReference))
        .mergeIntoScope(getIntermediatePackageObjects(classReference))
    }

    scopeWithTopPackages.mergeIntoScope(getPackageObject(classReference, declarations))
      .addToScope(declarations)
  }

  private def getTopLevelPackageObject(classReference: ClassReference): Variable = {
    val topLevelPackage = classReference.`package`.parents.head.parent

    Variable(topLevelPackage.toIdentifier, ScopeElement.const(topLevelPackage.packageObjectReference))
  }

  private def getIntermediatePackageObjects(classReference: ClassReference): Seq[ClassReferenceAndDefinition] = {
    classReference.`package`.parents.map(path => {
      val parentReference = path.parent.packageObjectReference
      val nestedReference = path.packageObjectReference

      ClassReferenceAndDefinition(
        parentReference, ClassDefinition(Map(path.path.last -> ScopeElement.const(nestedReference)))
      )
    })
  }

  private def getPackageObject(classReference: ClassReference, declarations: ScopePart) = {
    val packageObjectDefinition = ClassDefinition(declarations.functions.mapValues(ScopeElement.const))

    ClassReferenceAndDefinition(classReference.`package`.packageObjectReference, packageObjectDefinition)
  }
}
