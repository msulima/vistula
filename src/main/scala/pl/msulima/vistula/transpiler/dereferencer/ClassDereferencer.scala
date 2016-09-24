package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, Return}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._

trait ClassDereferencer {
  this: Dereferencer with FunctionDereferencer =>

  private val ConstructorId = Ast.identifier("__init__")
  private val ThisId = Ast.identifier("this")

  def classDereferencer(classDef: Ast.stmt.ClassDef) = {
    val identifier = classDef.name
    val body = classDef.body
    val constructorFunc = findConstructor(identifier, body)
    val fields = FunctionDef.mapArguments(constructorFunc.args)

    val methods = findMethods(body)

    val declarations = methods.map(method => {
      val definition = dereferenceFunction(method)

      method.name -> definition.`type`
    })

    val members = fields.map(field => {
      field.id -> field.`type`
    }).toMap ++ declarations

    val introduceConstructor = constructor(identifier, fields, constructorFunc)

    val definitions = methods.map(method => {
      val prototypeName = Ast.identifier(identifier.name + ".prototype." + method.name.name)

      Declare(prototypeName, Operation(method.copy(name = Ast.identifier("")), Seq()), mutable = false, declare = false)
    })

    (ClassDefinition(members), introduceConstructor, definitions.map(dereference))
  }

  private def findConstructor(classIdentifier: Ast.identifier, body: Seq[Ast.stmt]): Ast.stmt.FunctionDef = {
    body.collectFirst({
      case func: Ast.stmt.FunctionDef if func.name == ConstructorId =>
        func
    }).get
  }

  private def constructor(identifier: Ast.identifier, fields: Seq[Variable], constructorFunc: Ast.stmt.FunctionDef): Introduce = {
    val constructor = createConstructor(identifier, fields, constructorFunc.body)
    val definition = FunctionDefinition(fields.map(_.`type`), resultType = ScopeElement.const(ClassReference(Seq(identifier))), constructor = true)

    Introduce(Variable(identifier, ScopeElement.const(definition)), constructor)
  }

  private def createConstructor(identifier: Ast.identifier, fields: Seq[Variable], constructorBody: Seq[Ast.stmt]) = {
    val body = initializeFields(identifier, fields) ++ constructorBody.map(Tokenizer.applyStmt) :+ Operation(Return, Seq())

    FunctionDef(identifier, fields, body)
  }

  private def initializeFields(classIdentifier: identifier, arguments: Seq[Variable]): Seq[Token] = {
    val introduceThis = ImportVariable(Variable(ThisId, ScopeElement.const(ClassReference(classIdentifier.name))))
    val fieldInitialization = arguments.map(arg => {
      val source = Reference(Reference(ThisId), Constant(arg.id.name))
      Operation(Declare(declare = false, mutable = arg.`type`.observable), Seq(source, Reference(arg.id)))
    })

    introduceThis +: fieldInitialization
  }

  private def findMethods(body: Seq[Ast.stmt]): Seq[FunctionDef] = {
    body.collect({
      case func: Ast.stmt.FunctionDef if func.name != ConstructorId =>
        // FIXME hacky
        FunctionDef.apply(func).asInstanceOf[Operation].operator.asInstanceOf[FunctionDef]
    })
  }
}
