package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, Return}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._

trait ConstructorDereferencer {
  this: Dereferencer =>

  private val ThisId = Ast.identifier("this")
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
    val introduceConstructor = {
      val name = FunctionReference(`package`, identifier).toIdentifier
      val constructor = createConstructor(identifier, fields, constructorFunc.body)

      Declare(name, constructor, mutable = false, declare = `package` == Package.Root)
    }

    val variable = {
      val definition = FunctionDefinition(
        fields.map(_.`type`),
        resultType = ScopeElement.const(ClassReference(`package`, identifier)),
        constructor = true
      )

      Variable(identifier, ScopeElement.const(definition))
    }

    dereferencerImpl.dereferenceIntroduce(variable, introduceConstructor)
  }

  private def createConstructor(identifier: Ast.identifier, fields: Seq[Variable], constructorBody: Seq[Ast.stmt]) = {
    val body = initializeFields(identifier, fields) ++ constructorBody.map(Tokenizer.applyStmt) :+ Operation(Return, Seq())

    Operation(new FunctionDef(FunctionReference(`package`, identifier), body, fields), Seq())
  }

  private def initializeFields(classIdentifier: identifier, arguments: Seq[Variable]): Seq[Token] = {
    val introduceThis = ImportVariable(Variable(ThisId, ScopeElement.const(ClassReference(classIdentifier.name))))
    val fieldInitialization = arguments.map(arg => {
      val source = Reference(Reference(ThisId), arg.id)
      Operation(Declare(declare = false, mutable = arg.`type`.observable), Seq(source, Reference(arg.id)))
    })

    introduceThis +: fieldInitialization
  }
}
