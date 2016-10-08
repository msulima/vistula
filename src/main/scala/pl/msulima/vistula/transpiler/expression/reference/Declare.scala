package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassType, ScopeElement, Variable}

object Declare {

  def introduce(identifier: Ast.identifier, body: Token, typedef: ClassType, mutable: Boolean, declare: Boolean = true): Introduce = {
    val variable = Variable(identifier, ScopeElement(mutable, typedef))

    val apply1 = Operation(Declare(declare = declare, mutable = mutable), Seq(IdConstant(identifier), body))
    Introduce(variable, apply1)
  }

  def apply(identifier: Ast.identifier, body: Token, mutable: Boolean, declare: Boolean): Operation = {
    Operation(Declare(declare = declare, mutable = mutable), Seq(IdConstant(identifier), body))
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
