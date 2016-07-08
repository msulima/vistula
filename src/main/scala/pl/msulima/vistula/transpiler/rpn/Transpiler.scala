package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn

object Transpiler extends App {

  def apply: PartialFunction[Ast.stmt, Constant] = {
    rpn.Tokenizer.applyStmt.andThen(Dereferencer.apply).andThen(toConstant)
  }

  def applyExpr: PartialFunction[Ast.expr, Constant] = {
    rpn.Tokenizer.apply.andThen(Dereferencer.apply).andThen(toConstant)
  }

  private def toConstant(token: Token): Constant = {
    //    println("^", token)

    token match {
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
