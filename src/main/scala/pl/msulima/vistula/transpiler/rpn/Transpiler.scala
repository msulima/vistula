package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast


object Transpiler {

  def scoped(program: Seq[Ast.stmt]): String = {
    toJavaScript(Transformer.scoped(program))
  }

  def toJavaScript(program: Seq[Token]): String = {
    program.map(toConstant).map(_.value).mkString("", ";\n", ";")
  }

  private def toConstant(token: Token): Constant = {
    token match {
      case Box(op) =>
        BoxOp(List(), toConstant(op))
      case Observable(op) =>
        toConstant(op)
      case Operation(op@RxMapOp(_), operands, output) =>
        op(operands.map(toConstant).distinct.toList, toConstant(
          SubstituteObservables(
            operands.map(_.asInstanceOf[Observable]).distinct,
            output.asInstanceOf[Operation]
          ))
        )
      case Operation(operation, operands, output) =>
        operation.apply(operands.map(toConstant).toList, toConstant(output))
      case x: Constant => x
    }
  }
}
