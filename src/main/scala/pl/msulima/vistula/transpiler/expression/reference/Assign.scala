package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

case object Assign extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.AssignStmt(expr, value) =>
      Operation(Assign, Seq(Tokenizer.apply(expr), Box(Tokenizer.applyStmt(value))))
  }

  override def apply(operands: List[Constant]): Constant = {
    Constant(s"${operands(1).value}.rxForEachOnce($$arg => ${operands(0).value}.rxPush($$arg))")
  }
}
