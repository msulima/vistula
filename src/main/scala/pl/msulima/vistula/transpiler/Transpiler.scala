package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.DereferencerImpl


object Transpiler {

  def apply(token: Token): String = {
    toJavaScript(DereferencerImpl(Scope(Map()), token))
  }

  private def toJavaScript(token: Token): String = {
    Transpiler.toJavaScript(Seq(token)).dropRight(1)
  }

  def scoped(program: Seq[Ast.stmt]): String = {
    toJavaScript(Transformer.transform(program))
  }

  def toJavaScript(program: Seq[Token]): String = {
    program.map(toConstant).filterNot(_ == Tokenizer.Pass).map(_.value).mkString("", ";\n", ";")
  }

  private def toConstant(token: Token): Constant = {
    token match {
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
