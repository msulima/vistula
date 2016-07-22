package pl.msulima.vistula.transpiler.rpn.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.transpiler.rpn.expression.reference.FunctionCall

case object If {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      FunctionCall(Observable(Constant("vistula.ifStatement")), Seq(
        Tokenizer.boxed(testExpr),
        Box(Transformer.wrapAndReturnLast(body)),
        Box(Transformer.wrapAndReturnLast(orElse))
      ))
  }
}
