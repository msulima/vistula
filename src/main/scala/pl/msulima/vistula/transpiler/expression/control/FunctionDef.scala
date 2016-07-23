package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.util.Indent

case object FunctionDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) =>
          id.name
      })

      Operation(FunctionDef, Constant(name.name) +: argumentIds.map(Constant.apply), FunctionScope(body))
  }

  def anonymous(singleArg: String, body: Token): Token = {
    Operation(FunctionDef, Seq(Constant(""), Constant(singleArg)), body)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(
      s"""function ${operands.head.value}(${operands.tail.map(_.value).mkString(", ")}) {
          |${Indent.leftPad(output.value)}
          |}""".stripMargin)
  }
}

case object FunctionScope extends Operator {

  def apply(program: Seq[Ast.stmt]): Token = {
    val body = program.map(Tokenizer.applyStmt)

    Operation(FunctionScope, body.init :+ Box(body.last), Tokenizer.Ignored)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(Transpiler.toJavaScript(operands))
  }
}
