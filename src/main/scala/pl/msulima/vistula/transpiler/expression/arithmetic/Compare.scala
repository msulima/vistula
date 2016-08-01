package pl.msulima.vistula.transpiler.expression.arithmetic

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object Compare {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val symbol = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      Operation(BinOp(symbol), Seq(Tokenizer.apply(x), Tokenizer.apply(y)))
  }
}
