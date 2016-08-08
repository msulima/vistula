package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

case object Lambda {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Lambda(Ast.arguments(args, None), body) =>
      val argsNames = args.map(_.name)
      val transpiledBody = Tokenizer.apply(body)

      FunctionDef.anonymous(argsNames, Seq(transpiledBody), mutableArgs = true)
  }
}
