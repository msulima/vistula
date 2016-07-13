package pl.msulima.vistula.transpiler.rpn.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Operation, _}

object Declare {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable) =>
      val body = Tokenizer.applyStmt(stmt)

      val value = if (mutable) {
        Box(body)
      } else {
        Operation(Dereference, Seq(), body)
      }

      Operation(Declare(identifier, mutable), Seq(), value)
  }
}

case class Declare(identifier: Ast.identifier, mutable: Boolean) extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"const ${identifier.name} = ${output.value}")
  }
}
