package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.util.Indent

case object FunctionDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.argument(id, observable, fancyClassName, _) =>
          val className = if (fancyClassName.isEmpty) {
            ClassReference.Object
          } else {
            ClassReference(fancyClassName.map(_.name).mkString("."))
          }
          Variable(id, ScopeElement(observable = observable, className))
      })

      Introduce(
        Variable(name, ScopeElement(observable = false, FunctionDefinition(argumentIds.map(_.`type`), resultIsObservable = true))),
        FunctionDef(name, argumentIds, body.map(Tokenizer.applyStmt))
      )
  }

  def anonymous(body: Seq[Token]): Token = {
    anonymous(Seq(), body, mutableArgs = false)
  }

  def anonymous(singleArg: Ast.identifier, body: Seq[Token], mutableArgs: Boolean = true): Token = {
    anonymous(Seq(singleArg), body, mutableArgs = mutableArgs)
  }

  def anonymous(argumentIds: Seq[Ast.identifier], body: Seq[Token], mutableArgs: Boolean): Token = {
    val arguments = argumentIds.map(arg => Variable(arg, ScopeElement(observable = mutableArgs)))

    apply(Ast.identifier(""), arguments, body)
  }

  def apply(name: Ast.identifier, arguments: Seq[Variable], body: Seq[Token]): Token = {
    val declarations = arguments.map(arg => {
      Introduce(arg, Constant(""))
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
