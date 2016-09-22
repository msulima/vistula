package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionDef, FunctionScope, Return}
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, ScopeElement}

trait FunctionDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def functionDereferencer: PartialFunction[Token, Expression] = {
    case operation@Operation(func: FunctionDef, Nil) =>
      dereferenceFunction(func)
    case operation@Operation(FunctionScope, program) =>
      dereferenceScope(program, box = true)
  }

  def dereferenceFunction(func: FunctionDef): ExpressionOperation = {
    val body = dereferenceScope(func.program, box = false)
    val funcDefinition = FunctionDefinition(func.arguments.map(_.`type`), body.`type`)

    ExpressionOperation(func, Seq(body), ScopeElement(observable = false, funcDefinition))
  }

  private def dereferenceScope(program: Seq[Token], box: Boolean): ExpressionOperation = {
    val result = Transformer.scoped(program, scope)
    val maybeLast = findReturn(result, box)
    val body = result.init ++ maybeLast.toSeq

    ExpressionOperation(FunctionScope, body, body.last.`type`)
  }

  private def findReturn(result: Seq[Expression], box: Boolean): Option[ExpressionOperation] = {
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
