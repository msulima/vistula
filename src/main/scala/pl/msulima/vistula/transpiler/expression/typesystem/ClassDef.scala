package pl.msulima.vistula.transpiler.expression.typesystem

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.{identifier, stmt}
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, Return}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._

object ClassDef {

  private val ConstructorId = Ast.identifier("__init__")

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      val (fields, constructor) = findFields(identifier, body)
      val classReference = ClassReference(Seq(identifier))
      val definition = FunctionDefinition(fields.map(_.`type`),
        resultType = ScopeElement(observable = false, `type` = classReference), constructor = true)

      val introduceConstructor = Introduce(Variable(identifier, ScopeElement(observable = false, definition)), constructor)

      IntroduceClass(classReference, fields.map(field => {
        field.id -> field.`type`
      }).toMap, findMethods(body), introduceConstructor)
  }

  private def findFields(classIdentifier: Ast.identifier, body: Seq[stmt]): (Seq[Variable], Token) = {
    body.collectFirst({
      case func@Ast.stmt.FunctionDef(ConstructorId, args, constructorBody, _) =>
        val arguments = FunctionDef.mapArguments(args)

        arguments -> createConstructor(classIdentifier, constructorBody, arguments)
    }).get
  }

  private def createConstructor(classIdentifier: identifier, constructorBody: Seq[stmt], arguments: Seq[Variable]) = {
    val thisId = Ast.identifier("this")

    val introduceThis = Import(Variable(thisId, ScopeElement(observable = false, ClassReference(classIdentifier.name))))
    val fieldInitialization = arguments.map(arg => {
      val source = Reference(Reference(thisId), Constant(arg.id.name))
      Operation(Declare(declare = false, mutable = arg.`type`.observable), Seq(source, Reference(arg.id)))
    })

    val body = (introduceThis +: fieldInitialization) ++ constructorBody.map(Tokenizer.applyStmt) :+ Operation(Return, Seq())
    FunctionDef(classIdentifier, arguments, body)
  }

  private def findMethods(body: Seq[stmt]): Seq[FunctionDef] = {
    body.collect({
      case func: Ast.stmt.FunctionDef if func.name != ConstructorId =>
        // FIXME hacky
        FunctionDef.apply(func).asInstanceOf[Operation].operator.asInstanceOf[FunctionDef]
    })
  }
}
