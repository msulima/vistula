package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.function.FunctionDereferencer
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, Return}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._

trait ClassDereferencer {
  this: Dereferencer with DeclareDereferencer with FunctionDereferencer =>

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

      method.name.name -> definition.`type`
    })

    val members = fields.map(field => {
      field.id -> field.`type`
    }).toMap ++ declarations

    val dereferencerImpl = DereferencerImpl(scope.addToScope(ClassReference(`package`, identifier), ClassDefinition(members)), `package`)
    val definitions = getMethodDefinitions(dereferencerImpl, identifier, methods)

    val scopedResult = constructor(dereferencerImpl, identifier, fields, constructorFunc)

    scopedResult.copy(program = scopedResult.program ++ definitions)
  }

  private def findConstructor(classIdentifier: Ast.identifier, body: Seq[Ast.stmt]): Ast.stmt.FunctionDef = {
    body.collectFirst({
      case func: Ast.stmt.FunctionDef if func.name == ConstructorId =>
        func
    }).get
  }

  private def constructor(dereferencerImpl: DereferencerImpl, identifier: Ast.identifier, fields: Seq[Variable], constructorFunc: Ast.stmt.FunctionDef) = {
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
      val source = Reference(Reference(ThisId), Constant(arg.id.name))
      Operation(Declare(declare = false, mutable = arg.`type`.observable), Seq(source, Reference(arg.id)))
    })

    introduceThis +: fieldInitialization
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

      dereferencer.dereferenceDeclare(Constant(prototypeName.name), Operation(method, Seq()), mutable = false, declare = false)
    })
  }
}
