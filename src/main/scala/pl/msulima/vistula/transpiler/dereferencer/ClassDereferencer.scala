package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Declare
import pl.msulima.vistula.transpiler.scope.ClassDefinition

trait ClassDereferencer {
  this: Dereferencer with FunctionDereferencer =>

  def classDereferencer(introduceClass: IntroduceClass): (ClassDefinition, Seq[Expression]) = {
    val declarations = introduceClass.methods.map(method => {
      val definition = dereferenceFunction(method)

      method.name -> definition.`type`
    })

    val definitions = introduceClass.methods.map(method => {
      val prototypeName = Ast.identifier(introduceClass.id.name + ".prototype." + method.name.name)

      Declare(prototypeName, Operation(method.copy(name = Ast.identifier("")), Seq()), mutable = false, declare = false)
    })

    ClassDefinition(introduceClass.fields ++ declarations) -> definitions.map(dereference)
  }
}
