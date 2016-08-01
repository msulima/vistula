package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.{Identifier, Variable}
import pl.msulima.vistula.transpiler.{Operation, _}

object Declare extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable) =>
      val body = Tokenizer.applyStmt(stmt)

      Declare(identifier, mutable, body)
  }

  def apply(identifier: Ast.identifier, mutable: Boolean, body: Token) = {
    val value = if (mutable) {
      Box(body)
    } else {
      Operation(Dereference, Seq(), body)
    }

    Introduce(Variable(identifier, Identifier(mutable)), Operation(Declare, Seq(Constant(identifier.name)), value))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"const ${operands.head.value} = ${output.value}")
  }
}