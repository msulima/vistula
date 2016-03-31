package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr

sealed trait Variable {

  def prettyPrint(indent: Int = 0): String

  def reference: Ast.expr

  def expression: Ast.expr

  protected def ind(indent: Int) = "  " * indent
}

case class Constant(expression: Ast.expr) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} Const: $expression"
  }

  override def reference: expr = expression
}

case class NamedObservable(name: String) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} Named: $name"
  }

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)

  override def expression: expr = reference
}

case class Observable(name: String, expression: Ast.expr) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} $name = $expression"
  }

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)
}
