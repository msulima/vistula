package pl.msulima.vistula.transpiler.rpn.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.util.Indent

object FunctionDef extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) =>
          id.name
      })

      Operation(FunctionDef, Constant(name.name) +: argumentIds.map(Constant.apply), Transformer.returnLast(body))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(
      s"""function ${operands.head.value}(${operands.tail.map(_.value).mkString(", ")}) {
          |${Indent.leftPad(output.value)}
          |}""".stripMargin)
  }
}
