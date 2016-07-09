package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._

object Lambda extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Lambda(Ast.arguments(args, None, None, Seq()), body) =>
      val argsNames = args.map({
        case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Param) =>
          Constant(x)
      }).toList
      val transpiledBody = Tokenizer.apply(body)

      Operation(Lambda, argsNames, transpiledBody)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"(${operands.map(_.value).mkString(", ")}) => ${output.value}")
  }
}
