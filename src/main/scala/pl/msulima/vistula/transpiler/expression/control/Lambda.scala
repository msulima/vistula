package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

case object Lambda {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Lambda(args, body) =>
      val transpiledBody = Tokenizer.apply(body)

      FunctionDef.anonymous(FunctionDef.mapArguments(args), Seq(transpiledBody))
  }
}
