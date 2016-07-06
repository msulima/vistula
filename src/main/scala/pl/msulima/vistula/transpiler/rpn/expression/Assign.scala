package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Operation, _}

object Assign {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable) =>
      val body = Tokenizer.applyStmt(stmt)

      val value = if (mutable) {
        Box(body)
      } else {
        body
      }

      Operation(Assign(identifier), Seq(value))
  }
}

case class Assign(identifier: Ast.identifier) extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    Constant(s"const ${identifier.name} = ${operands.head.value}")
  }
}
