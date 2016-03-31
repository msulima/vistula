package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr

sealed trait Variable {

  def prettyPrint(indent: Int = 0): String

  def reference: Ast.expr

  def expression: Ast.expr

  def dependencies: Set[Variable]

  protected def ind(indent: Int) = "  " * indent
}

case class Constant(expression: Ast.expr) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"${ind(indent)} Const: $expression\n"
  }

  override def reference: expr = expression

  override def dependencies: Set[Variable] = Set.empty
}

case class NamedObservable(name: String) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"${ind(indent)} NamedObservable: $name\n"
  }

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)

  override def expression: expr = reference

  override def dependencies: Set[Variable] = Set.empty
}

case class Observable(name: String, expression: Ast.expr, dependencies: Set[Variable]) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"""${ind(indent)} $name = $expression
       |${dependencies.map(_.prettyPrint(indent + 1)).mkString("")}""".stripMargin
  }

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)
}
