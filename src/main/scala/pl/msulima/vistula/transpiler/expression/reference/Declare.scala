package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement, Variable}

case object Declare extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable, typedef) =>
      val body = Tokenizer.applyStmt(stmt)

      Declare(identifier, mutable, body, ClassReference(typedef))
  }

  def apply(identifier: Ast.identifier, mutable: Boolean, body: Token, typedef: ClassReference) = {
    val value = if (mutable) {
      Box(body)
    } else {
      Operation(Dereference, Seq(body))
    }

    Introduce(Variable(identifier, ScopeElement(mutable, typedef)), Operation(Declare, Seq(Constant(identifier.name), value)))
  }

  override def apply(operands: List[Constant]): String = {
    s"const ${operands(0).value} = ${operands(1).value}"
  }
}
