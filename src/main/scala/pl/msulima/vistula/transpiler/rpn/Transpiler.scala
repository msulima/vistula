package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn

object Transpiler extends App {

  def apply: PartialFunction[Ast.expr, Constant] = {
    rpn.Tokenizer.apply.andThen(Dereferencer.apply).andThen(x => {
      println(x)
      println("")
      toConstant(x)
    })
  }

  private def toConstant(token: Token): Constant = {
    token match {
      case Operation(op@RxMapOp(boxes), operands) =>
        op(operands.map(toConstant).toList)
      case Operation(operation, operands) =>
        operation.apply(operands.map(toConstant).toList)
      case x: Constant => x
      case x: Reference => Constant(x.value)
    }
  }
}
