package pl.msulima.vistula.transpiler.expression.typesystem

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}

object ClassDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      val constructor: Token = createConstructor(identifier, body)

      constructor
  }

  private def createConstructor(classIdentifier: Ast.identifier, body: Seq[stmt]): Token = {
    body.collectFirst({
      case func@Ast.stmt.FunctionDef(Ast.identifier("__init__"), args, constructorBody, _) =>
        val arguments = FunctionDef.mapArguments(args)

        val introduceThis = Introduce(Variable(Ast.identifier("this"), ScopeElement(observable = false)), Constant(""))
        val fieldInitialization = arguments.map(arg => {
          val source = Reference(Reference(Ast.identifier("this")), Constant(arg.id.name))
          Operation(Declare(declare = false), Seq(source, Reference(arg.id)))
        })

        FunctionDef(classIdentifier, arguments, (introduceThis +: fieldInitialization) ++ constructorBody.map(Tokenizer.applyStmt))
    }).get
  }

  override def apply(inputs: List[Constant]): String = "wat"
}
