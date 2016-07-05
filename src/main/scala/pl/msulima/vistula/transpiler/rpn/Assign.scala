package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Assign {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, value, mutable) =>
      Operation(Assign(identifier), Seq(Box(Tokenizer.applyStmt(value))))
  }
}

case class Assign(identifier: Ast.identifier) extends Operator {

  override def apply(operands: List[Constant]): Constant = {
    Constant(s"const ${identifier.name} = ${operands.head.value}")
  }
}
