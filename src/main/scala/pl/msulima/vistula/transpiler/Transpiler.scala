package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast


object Transpiler {

  def scoped(program: Seq[Ast.stmt]): String = {
    toJavaScript(Transformer.transform(program))
  }

  def toJavaScript(program: Seq[Expression]): String = {
    program.map(toConstant).filterNot(_ == Tokenizer.Pass).map(_.value).mkString("", ";\n", ";")
  }

  private def toConstant(token: Expression): Constant = {
    token match {
      case ExpressionOperation(op@ExpressionMap(output), operands, _) =>
        op(operands.map(toConstant).distinct.toList, toConstant(
          SubstituteObservables(operands.distinct, output))
        )
      case ExpressionOperation(operation, operands, _) =>
        operation.apply(operands.map(toConstant).toList, Constant("FUUUUU"))
      case ExpressionConstant(value, _) =>
        Constant(value)
    }
  }
}
