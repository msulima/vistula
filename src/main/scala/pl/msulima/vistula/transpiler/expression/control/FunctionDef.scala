package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.util.Indent

object FunctionDef {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case funcDef@Ast.stmt.FunctionDef(name, args, body, _) =>
      Direct(funcDef)
  }

  def mapArguments(arguments: Ast.arguments) = {
    arguments.args.map({
      case Ast.argument(id, observable, fancyClassName, _) =>
        Variable(id, ScopeElement(observable = observable, ClassReference(fancyClassName)))
    })
  }

  def anonymous(singleArg: Variable, body: Seq[Token]): Token = {
    Operation(new FunctionDef(FunctionReference.Anonymous, body, Seq(singleArg)), Seq())
  }
}

case class FunctionDef(name: FunctionReference, program: Seq[Token], arguments: Seq[Variable]) extends Operator {

  override def apply(operands: List[Constant]): String = {
    val body = operands.head

    s"""function ${name.name.name}(${arguments.map(_.id.name).mkString(", ")}) {
        |${Indent.leftPad(body.value)}
        |}""".stripMargin
  }
}

case class FunctionDef2(name: FunctionReference, arguments: Seq[Variable]) extends Operator {

  override def apply(operands: List[Constant]): String = {
    val body = operands.head

    s"""function ${name.name.name}(${arguments.map(_.id.name).mkString(", ")}) {
        |${Indent.leftPad(body.value)}
        |}""".stripMargin
  }
}

case object FunctionScope extends Operator {

  override def apply(operands: List[Constant]): String = {
    Transpiler.toJavaScriptFromTokens(operands)
  }
}
