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
        Constant(operation.apply(operands.map(toConstant).toList))
      case ExpressionConstant(value, _) =>
        Constant(value)
    }
  }

  private def mapToConstant(operator: Operator, output: ExpressionOperation, operands: Seq[Expression]): Constant = {
    val mappedOutput = toConstant(SubstituteObservables(output, operands.distinct))
    val mappedInputs = operands.map(toConstant).distinct.toList
    Constant(operator(mappedOutput +: mappedInputs))
  }
}
