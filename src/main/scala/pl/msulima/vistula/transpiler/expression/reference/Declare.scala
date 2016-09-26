package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ClassType, ScopeElement, Variable}

object Declare {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.DeclareStmt(identifier, stmt, mutable, typedef) =>
      val body = Tokenizer.applyStmt(stmt)

      Declare.introduce(identifier, body, ClassReference(typedef), mutable = mutable)
  }

  def introduce(identifier: Ast.identifier, body: Token, typedef: ClassType, mutable: Boolean, declare: Boolean = true): Token = {
    val variable = Variable(identifier, ScopeElement(mutable, typedef))

    Introduce(variable, apply(identifier, body, mutable = mutable, declare = declare))
  }

  def apply(identifier: Ast.identifier, body: Token, mutable: Boolean, declare: Boolean): Operation = {
    Operation(Declare(declare = declare, mutable = mutable), Seq(Constant(identifier.name), body))
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
