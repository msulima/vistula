package pl.msulima.vistula.transpiler.dereferencer.function

import pl.msulima.vistula.transpiler.dereferencer.BoxDereferencer
import pl.msulima.vistula.transpiler.expression.control.Return
import pl.msulima.vistula.transpiler.{Expression, ExpressionOperation}


trait ReturnDereferencer {
  this: BoxDereferencer =>

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
      Some(ExpressionOperation(Return, Seq(y), y.`type`))
    } else {
      Some(ExpressionOperation(Return, Seq(x), x.`type`))
    }
  }
}