package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Operation, _}

object Assign extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable) =>
      val body = Tokenizer.applyStmt(stmt)

      val value = if (mutable) {
        Box(body)
      } else {
        body
      }

      Operation(Declare(identifier, mutable), Seq(), value)
    case Ast.stmt.AssignStmt(expr, value) =>
      Operation(Assign, Seq(Tokenizer.boxed(value)), Tokenizer.apply(expr))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}.rxForEachOnce($$arg => ${output.value}.rxPush($$arg))")
  }
}

case class Declare(identifier: Ast.identifier, mutable: Boolean) extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"const ${identifier.name} = ${output.value}")
  }
}
