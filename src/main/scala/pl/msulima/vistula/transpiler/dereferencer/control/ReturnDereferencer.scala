package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.BoxDereferencer


trait ReturnDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def returnDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Return(Some(value))) =>
      val body = dereference(value)
      ExpressionOperation(Return, body)
  }

  def findReturn(result: Seq[Expression], box: Boolean): Option[ExpressionOperation] = {
    result.last match {
      case ExpressionOperation(Return, Nil, _) =>
        None
      case ExpressionOperation(Return, x :: Nil, _) =>
        doReturn(x, box)
      case x =>
        doReturn(x, box)
    }
  }

  private def doReturn(x: Expression, box: Boolean) = {
    if (box) {
      val y = toObservable(x)
      Some(ExpressionOperation(Return, y))
    } else {
      Some(ExpressionOperation(Return, x))
    }
  }
}

case object Return extends Operator {

  override def apply(operands: List[Constant]): String = {
    s"return ${operands.head.value}"
  }
}
