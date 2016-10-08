package pl.msulima.vistula.transpiler.dereferencer.data

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.control.{FunctionDereferencer, Return}
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference
import pl.msulima.vistula.transpiler.dereferencer.reference.Declare
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.scope._

trait ConstructorDereferencer {
  this: Dereferencer with FunctionDereferencer =>

  private val JavaScriptThisId = Ast.identifier("this")
  val ConstructorId = Ast.identifier("__init__")
  private val DefaultConstructor = Ast.stmt.FunctionDef(ConstructorId, Ast.arguments(Seq(), None), Seq(Ast.stmt.Pass), Seq())

  def findConstructor(classIdentifier: Ast.identifier, body: Seq[Ast.stmt]): Ast.stmt.FunctionDef = {
    body.collectFirst({
      case func: Ast.stmt.FunctionDef if func.name == ConstructorId =>
        func
    }).getOrElse(DefaultConstructor)
  }

  def constructor(dereferencerImpl: DereferencerImpl, identifier: Ast.identifier,
                  fields: Seq[Variable], constructorFunc: Ast.stmt.FunctionDef) = {
    val variable = {
      val definition = FunctionDefinition(
        fields.map(_.`type`),
        resultType = ScopeElement.const(ClassReference(`package`, identifier)),
        constructor = true
      )

      Variable(identifier, ScopeElement.const(definition))
    }

    val introduceConstructor = {
      val name = ExpressionConstant(FunctionReference(`package`, identifier).toIdentifier.name, ScopeElement.DefaultConst)
      val body = initializeFields(identifier, fields) ++ constructorFunc.body.map(Tokenizer.applyStmt) :+ Operation(Return, Seq())

      val constructor = dereferencerImpl.dereferenceFunction(FunctionReference(`package`, identifier), fields, body)

      dereferencerImpl.dereferenceDeclare(name, constructor, mutable = false, declare = `package` == Package.Root)
    }

    dereferencerImpl.dereferenceIntroduce(variable, introduceConstructor)
  }

  private def initializeFields(classIdentifier: identifier, arguments: Seq[Variable]): Seq[Token] = {
    arguments.map(arg => {
      val source = Reference(Reference(JavaScriptThisId), arg.id)
      Operation(Declare(declare = false, mutable = arg.`type`.observable), Seq(source, Reference(arg.id)))
    })
  }
}
