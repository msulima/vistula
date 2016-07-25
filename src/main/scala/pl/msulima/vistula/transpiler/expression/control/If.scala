package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

case object If {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      FunctionCall(Constant("vistula.ifStatement"), Seq(
        Tokenizer.apply(testExpr),
        Transformer.wrapAndReturnLast(body),
        Transformer.wrapAndReturnLast(orElse)
      ))
  }
}
