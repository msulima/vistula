package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Operation, _}

case object Assign extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.AssignStmt(expr, value) =>
      Operation(Assign, Seq(Box(Tokenizer.applyStmt(value))), Tokenizer.apply(expr))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}.rxForEachOnce($$arg => ${output.value}.rxPush($$arg))")
  }
}
