package pl.msulima.vistula.transpiler.expression.typesystem

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.{identifier, stmt}
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._

object ClassDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      val (fields, constructor) = findFields(identifier, body)
      val classReference = ClassReference(Seq(identifier))
      val definition = FunctionDefinition(fields.map(_.`type`),
        resultType = ScopeElement(observable = false, `type` = classReference), constructor = true)

      val classDefinition = ClassDefinition(fields.map(field => {
        field.id -> field.`type`
      }).toMap)

      IntroduceClass(classReference, classDefinition, Introduce(
        Variable(identifier, ScopeElement(observable = false, definition)),
        constructor
      ))
  }

  private def findFields(classIdentifier: Ast.identifier, body: Seq[stmt]): (Seq[Variable], Token) = {
    body.collectFirst({
      case func@Ast.stmt.FunctionDef(Ast.identifier("__init__"), args, constructorBody, _) =>
        val arguments = FunctionDef.mapArguments(args)

        arguments -> createConstructor(classIdentifier, constructorBody, arguments)
    }).get
  }

  private def createConstructor(classIdentifier: identifier, constructorBody: Seq[stmt], arguments: Seq[Variable]) = {
    val thisId = Ast.identifier("this")

    val introduceThis = Import(Variable(thisId, ScopeElement(observable = false)))
    val fieldInitialization = arguments.map(arg => {
      val source = Reference(Reference(thisId), Constant(arg.id.name))
      Operation(Declare(declare = false), Seq(source, Reference(arg.id)))
    })

    FunctionDef(classIdentifier, arguments, (introduceThis +: fieldInitialization) ++ constructorBody.map(Tokenizer.applyStmt))
  }

  override def apply(inputs: List[Constant]): String = "wat"
}
