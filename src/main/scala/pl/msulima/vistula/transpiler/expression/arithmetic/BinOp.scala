package pl.msulima.vistula.transpiler.expression.arithmetic

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object BinOp {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.BinOp(x, op, y) =>
      val symbol = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }

      Operation(BinOp(symbol), Seq(Tokenizer.apply(x), Tokenizer.apply(y)))
  }
}

case class BinOp(symbol: String) extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    operands match {
      case left :: right :: Nil =>
        Constant(s"${left.value} $symbol ${right.value}")
    }
  }
}
