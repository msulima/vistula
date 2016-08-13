package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.util.Indent

case object FunctionDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, args, body, _) =>
      val arguments = mapArguments(args)
      val definition = FunctionDefinition(arguments.map(_.`type`), resultType = ScopeElement.Default)

      Introduce(
        Variable(name, ScopeElement(observable = false, definition)),
        FunctionDef(name, arguments, body.map(Tokenizer.applyStmt))
      )
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
    val declarations = arguments.map(arg => {
      Import(arg)
    })

    Operation(
      FunctionDef,
      Constant(name.name) +: FunctionScope(declarations ++ body) +: arguments.map(arg => Constant(arg.id.name))
    )
  }

  override def apply(operands: List[Constant]): String = {
    val name = operands(0)
    val body = operands(1)
    val arguments = operands.drop(2)

    s"""function ${name.value}(${arguments.map(_.value).mkString(", ")}) {
        |${Indent.leftPad(body.value)}
        |}""".stripMargin
  }
}

case object FunctionScope extends Operator {

  private[control] def apply(program: Seq[Token]): Token = {
    Operation(FunctionScope, program.init :+ Box(program.last))
  }

  override def apply(operands: List[Constant]): String = {
    Transpiler.toJavaScriptFromTokens(operands)
  }
}
