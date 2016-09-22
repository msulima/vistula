package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.ClassDefinition

trait ClassDereferencer {
  this: FunctionDereferencer =>

  def classDereferencer(introduceClass: IntroduceClass) = {
    val operations = introduceClass.methods.map(method => {
      method.name -> dereferenceFunction(method).`type`
    }).toMap

    ClassDefinition(introduceClass.fields ++ operations)
  }
}
