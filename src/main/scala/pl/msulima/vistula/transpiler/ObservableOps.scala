package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

case object WrapScope extends Operator {

  def apply(program: Seq[Ast.stmt]): Token = {
    Operation(WrapScope, program.map(Tokenizer.applyStmt))
  }

  override def apply(operands: List[Constant]): Constant = {
    if (operands.size == 1) {
      operands.head
    } else {
      Constant(
        s"""vistula.wrap(() => {
            |${Indent.leftPad(Transpiler.toJavaScriptFromTokens(operands))}
            |})""".stripMargin)
    }
  }
}

