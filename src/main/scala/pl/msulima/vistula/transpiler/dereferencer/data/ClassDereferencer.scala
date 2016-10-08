package pl.msulima.vistula.transpiler.dereferencer.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.control.{FunctionDef, FunctionDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.reference.DeclareDereferencer
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.scope._

trait ClassDereferencer {
  this: Dereferencer with ConstructorDereferencer with DeclareDereferencer with FunctionDereferencer =>


  def classDereferencer(classDef: Ast.stmt.ClassDef) = {
    val identifier = classDef.name
    val body = classDef.body
    val constructorFunc = findConstructor(identifier, body)
    val fields = FunctionDef.mapArguments(constructorFunc.args)

    val methods = findMethods(body)

    val declarations = methods.map(method => {
      val definition = dereferenceFunction(method)

      method.name.name -> definition.`type`
    })

    val members = fields.map(field => {
      field.id -> field.`type`
    }).toMap ++ declarations

    val dereferencerImpl = DereferencerImpl(scope.addToScope(ClassReferenceAndDefinition(ClassReference(`package`, identifier), ClassDefinition(members))), `package`)
    val definitions = getMethodDefinitions(dereferencerImpl, identifier, methods)

    val scopedResult = constructor(dereferencerImpl, identifier, fields, constructorFunc)

    scopedResult.copy(program = scopedResult.program ++ definitions)
  }

  private def findMethods(body: Seq[Ast.stmt]): Seq[FunctionDef] = {
    body.collect({
      case func: Ast.stmt.FunctionDef if func.name != ConstructorId =>
        toFunctionDef(func)
    })
  }

  private def getMethodDefinitions(dereferencer: DeclareDereferencer, identifier: Ast.identifier, methods: Seq[FunctionDef]): Seq[Expression] = {
    methods.map(method => {
      val prototypeName = FunctionReference(`package`, Ast.identifier(identifier.name + ".prototype." + method.name.name.name)).toIdentifier

      dereferencer.dereferenceDeclare(IdConstant(prototypeName), Operation(method, Seq()), mutable = false, declare = false)
    })
  }
}
