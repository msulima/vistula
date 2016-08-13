package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement, Variable}

object Declare {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable, typedef) =>
      val body = Tokenizer.applyStmt(stmt)

      Declare(identifier, mutable, body, ClassReference(typedef))
  }

  def apply(identifier: Ast.identifier, mutable: Boolean, body: Token, typedef: ClassReference): Token = {
    val variable = Variable(identifier, ScopeElement(mutable, typedef))

    Introduce(variable, Operation(Declare(declare = true, mutable = mutable), Seq(Constant(identifier.name), body)))
  }
}

case class Declare(declare: Boolean, mutable: Boolean) extends Operator {

  override def apply(operands: List[Constant]): String = {
    val prefix = if (declare) {
      "const "
    } else {
      ""
    }

    s"$prefix${operands(0).value} = ${operands(1).value}"
  }
}
