package pl.msulima.vistula.transpiler.rpn.expression.arithmetic

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}


case object BinOp extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.BinOp(x, op, y) =>
      val symbol = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
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
