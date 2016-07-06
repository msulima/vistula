package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._

object Dereference {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Dereference(value) =>
      Operation(UnboxOp, Seq(Box(Tokenizer.apply(value))))
  }
}
