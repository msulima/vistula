package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

case object Lambda {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Lambda(Ast.arguments(args, None, Seq()), body) =>
      val argsNames = args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) =>
          id
      }).toList
      val transpiledBody = Tokenizer.apply(body)

      FunctionDef.anonymous(argsNames, Seq(transpiledBody), mutableArgs = true)
  }
}
