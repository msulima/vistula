package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
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
      val classReference = ClassReference(identifier.name)
      val declarations = Vistula.loadFile(classReference).declarations

      val packageObjectClassReference = ClassReference(classReference.`package`, Ast.identifier("$Object"))
      val packageObjectDefinition = ClassDefinition(declarations.functions.collect({
        case (Constant(id), func) if func.constructor =>
          (Ast.identifier(id), ScopeElement(observable = false, func))
      }))

      val scopeWithPackageObject = scope.addToScope(packageObjectClassReference, packageObjectDefinition)

      val right = classReference.`package`.path.inits.toList.dropRight(2)

      val scopeWithIntemediatePackageObjects = right.foldLeft(scopeWithPackageObject)({
        case (acc, path) =>
          val parentReference = ClassReference(Package(path.init), Ast.identifier("$Object"))
          val nestedReference = ClassReference(Package(path), Ast.identifier("$Object"))

          val definition = ClassDefinition(Map(path.last -> ScopeElement(observable = false, nestedReference)))
          val variable = Variable(parentReference.`package`.toIdentifier, ScopeElement.const(parentReference))

          // FIXME should merge with other modules
          acc.addToScope(parentReference, definition).addToScope(variable)
      })

      val ns = declarations.classes.foldLeft(scopeWithIntemediatePackageObjects)({
        case (acc, (id, definition)) =>
          acc.addToScope(id, definition)
      })

      ScopedResult(ns, Seq())
  }
}
