package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.ClassReference

trait ArithmeticDereferencer {
  this: Dereferencer with OperationDereferencer =>

  def arithmeticDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.UnaryOp(Ast.unaryop.Not, operandExpr))) =>
      val operand = dereference(operandExpr)

      dereferenceOperation(UnaryOp, Seq(operand), ClassReference.Boolean)
    case Direct(Ast.stmt.Expr(Ast.expr.BoolOp(op, xs))) =>
      val symbol = op match {
        case Ast.boolop.Or => "||"
        case Ast.boolop.And => "&&"
      }

      dereferenceOperation(BinOp(symbol), xs.map(dereference), ClassReference.Boolean)
    case Direct(Ast.stmt.Expr(Ast.expr.Compare(x, op +: _, y +: _))) =>
      val symbol = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      dereferenceOperation(BinOp(symbol), Seq(dereference(x), dereference(y)), ClassReference.Boolean)
    case Direct(Ast.stmt.Expr(Ast.expr.BinOp(x, op, y))) =>
      val symbol = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }

      dereferenceOperation(BinOp(symbol), Seq(dereference(x), dereference(y)), ClassReference.Integer)
  }
}

case object UnaryOp extends Operator {

  override def apply(operands: List[Constant]): String = {
    s"!(${operands.head.value})"
  }
}

case class BinOp(symbol: String) extends Operator {

  override def apply(operands: List[Constant]): String = {
    operands match {
      case left :: right :: Nil =>
        s"${left.value} $symbol ${right.value}"
    }
  }
}
