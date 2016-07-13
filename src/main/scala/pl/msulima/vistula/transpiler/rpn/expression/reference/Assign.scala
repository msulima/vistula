package pl.msulima.vistula.transpiler.rpn.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Operation, _}

object Assign extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.AssignStmt(expr, value) =>
      Operation(Assign, Seq(Tokenizer.boxed(value)), Tokenizer.apply(expr))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}.rxForEachOnce($$arg => ${output.value}.rxPush($$arg))")
  }
}
