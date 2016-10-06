package pl.msulima.vistula.transpiler.dereferencer.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.BoxDereferencer
import pl.msulima.vistula.transpiler.expression.data.{StaticDict, StaticString}
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait DictDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def dictDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Dict(keys, values))) =>
      val dict: Seq[Expression] = keys.zip(values).flatMap({
        case (Ast.expr.Str(key), expr) =>
          val keyExpression = StaticString.toExpression(key)
          val valueExpression = toObservable(dereference(Tokenizer.apply(expr)))
          Seq(keyExpression, valueExpression)
      })

      ExpressionOperation(StaticDict, dict, ScopeElement.const(ClassReference.Object))
  }
}
