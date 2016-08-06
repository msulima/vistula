package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{FunctionDefinitionHelper, ScopeElement, Variable}
import pl.msulima.vistula.util.Indent

case object FunctionDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) =>
          id
      })

      Introduce(
        Variable(name, ScopeElement(observable = false, FunctionDefinitionHelper.adapt(argumentIds.size, argumentsAreObservable = true, resultIsObservable = true))),
        FunctionDef(name, argumentIds, body.map(Tokenizer.applyStmt))
      )
  }

  def anonymous(body: Seq[Token]): Token = {
    anonymous(Seq(), body, mutableArgs = false)
  }

  def anonymous(singleArg: Ast.identifier, body: Seq[Token], mutableArgs: Boolean = true): Token = {
    anonymous(Seq(singleArg), body, mutableArgs = mutableArgs)
  }

  def anonymous(firstArg: Ast.identifier, secondArg: Ast.identifier, body: Seq[Token], mutableArgs: Boolean): Token = {
    anonymous(Seq(firstArg, secondArg), body, mutableArgs)
  }

  def anonymous(arguments: Seq[Ast.identifier], body: Seq[Token], mutableArgs: Boolean): Token = {
    apply(Ast.identifier(""), arguments, body, mutableArgs)
  }

  def apply(name: Ast.identifier, arguments: Seq[Ast.identifier], body: Seq[Token], mutableArgs: Boolean = true): Token = {
    val declarations = arguments.map(arg => {
      Introduce(Variable(arg, ScopeElement(observable = mutableArgs)), Constant(""))
    })

    Operation(
      FunctionDef,
      Constant(name.name) +: FunctionScope(declarations ++ body) +: arguments.map(arg => Constant(arg.name))
    )
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val name = operands(0)
    val body = operands(1)
    val arguments = operands.drop(2)

    Constant(
      s"""function ${name.value}(${arguments.map(_.value).mkString(", ")}) {
          |${Indent.leftPad(body.value)}
          |}""".stripMargin)
  }
}

case object FunctionScope extends Operator {

  private[control] def apply(program: Seq[Token]): Token = {
    Operation(FunctionScope, program.init :+ Box(program.last))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(Transpiler.toJavaScriptFromTokens(operands))
  }
}
