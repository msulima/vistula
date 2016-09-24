package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.util.Indent

object FunctionDef {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, args, body, _) =>
      val arguments = mapArguments(args)

      FunctionDef(name, arguments, body.map(Tokenizer.applyStmt))
  }

  def mapArguments(arguments: Ast.arguments) = {
    arguments.args.map({
      case Ast.argument(id, observable, fancyClassName, _) =>
        Variable(id, ScopeElement(observable = observable, ClassReference(fancyClassName)))
    })
  }

  def anonymous(body: Seq[Token]): Token = {
    anonymous(Seq(), body)
  }

  def anonymous(singleArg: Variable, body: Seq[Token]): Token = {
    anonymous(Seq(singleArg), body)
  }

  def anonymous(arguments: Seq[Variable], body: Seq[Token]): Token = {
    apply(Ast.identifier(""), arguments, body)
  }

  def apply(name: Ast.identifier, arguments: Seq[Variable], body: Seq[Token]): Token = {
    val declarations = arguments.map(ImportVariable)

    Operation(FunctionDef(name, declarations ++ body, arguments), Seq())
  }
}

case class FunctionDef(name: Ast.identifier, program: Seq[Token], arguments: Seq[Variable]) extends Operator {

  override def apply(operands: List[Constant]): String = {
    val body = operands.head

    s"""function ${name.name}(${arguments.map(_.id.name).mkString(", ")}) {
        |${Indent.leftPad(body.value)}
        |}""".stripMargin
  }
}

case object FunctionScope extends Operator {

  override def apply(operands: List[Constant]): String = {
    Transpiler.toJavaScriptFromTokens(operands)
  }
}
