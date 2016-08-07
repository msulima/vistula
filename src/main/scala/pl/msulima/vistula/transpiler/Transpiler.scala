package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast


object Transpiler {

  def scoped(program: Seq[Ast.stmt]): String = {
    toJavaScript(Transformer.transform(program))
  }

  def toJavaScript(program: Seq[Expression]): String = {
    toJavaScriptFromTokens(program.map(toConstant))
  }

  def toJavaScriptFromTokens(program: Seq[Constant]): String = {
    program.filterNot(_ == Tokenizer.Pass).map(_.value).mkString("", ";\n", ";")
  }

  private def toConstant(token: Expression): Constant = {
    token match {
      case ExpressionOperation(op@RxMap(output), operands, _) =>
        mapToConstant(op, output, operands)
      case ExpressionOperation(op@RxFlatMap(output), operands, _) =>
        mapToConstant(op, output, operands)
      case ExpressionOperation(operation, operands, _) =>
        operation.apply(operands.map(toConstant).toList, Constant("FUUUUU"))
      case ExpressionConstant(value, _) =>
        Constant(value)
    }
  }

  private def mapToConstant(operator: Operator, output: ExpressionOperation, operands: Seq[Expression]): Constant = {
    operator(operands.map(toConstant).distinct.toList, toConstant(
      SubstituteObservables(output, operands.distinct))
    )
  }
}
