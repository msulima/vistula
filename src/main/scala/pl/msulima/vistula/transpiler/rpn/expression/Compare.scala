package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}


case object Compare extends Operator {

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

      Operation(BinOp, Seq(Tokenizer.apply(x), Tokenizer.apply(y)), Constant(symbol))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    operands match {
      case left :: right :: Nil =>
        Constant(s"${left.value} ${output.value} ${right.value}")
    }
  }
}
