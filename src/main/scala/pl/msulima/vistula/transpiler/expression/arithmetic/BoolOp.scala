package pl.msulima.vistula.transpiler.expression.arithmetic

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object BoolOp {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.BoolOp(op, xs) =>
      val symbol = op match {
        case Ast.boolop.Or => "||"
        case Ast.boolop.And => "&&"
      }

      Operation(BinOp(symbol), xs.map(Tokenizer.apply))
  }
}
