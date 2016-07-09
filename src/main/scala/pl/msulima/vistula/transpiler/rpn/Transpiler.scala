package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression.Assign
import pl.msulima.vistula.transpiler.{Scope, rpn}

object Transpiler extends App {

  def scoped(program: Seq[Ast.stmt]): String = {
    program.foldLeft(ScopedResult(Scope(Seq(), Seq(), mutable = false), Constant("")))((acc, stmt) => {
      val result = apply(acc.scope)(stmt)

      result.copy(token = Constant(acc.token.value + result.token.value + ";\n"))
    }).token.value.dropRight(1)
  }

  def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    rpn.Tokenizer.applyStmt.andThen(Dereferencer.apply(scope)).andThen(extractScope(scope))
  }

  def applyExpr(scope: Scope): PartialFunction[Ast.expr, ScopedResult] = {
    rpn.Tokenizer.apply.andThen(Dereferencer.apply(scope)).andThen(extractScope(scope))
  }

  private def extractScope(currentScope: Scope)(token: Token) = {
    token match {
      case Operation(Assign(identifier, mutable), _, _) =>
        val nextScope = if (mutable) {
          currentScope.copy(observables = currentScope.observables :+ identifier)
        } else {
          currentScope.copy(variables = currentScope.variables :+ identifier)
        }

        ScopedResult(nextScope, toConstant(token))
      case _ =>
        ScopedResult(currentScope, toConstant(token))
    }
  }

  private def toConstant(token: Token): Constant = {
    //    println("^", token)

    token match {
      case Box(op) =>
        BoxOp(List(), toConstant(op))
      case Observable(op) =>
        toConstant(op)
      case Operation(op@RxMapOp(_), operands, output) =>
        op(operands.map(toConstant).toList, toConstant(
          SubstituteObservables(
            operands.map(_.asInstanceOf[Observable]),
            output.asInstanceOf[Operation]
          ))
        )
      case Operation(operation, operands, output) =>
        operation.apply(operands.map(toConstant).toList, toConstant(output))
      case x: Constant => x
    }
  }
}
