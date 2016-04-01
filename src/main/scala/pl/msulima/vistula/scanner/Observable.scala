package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.{expr, stmt}
import pl.msulima.vistula.scanner.Ind.ind

object Ind {
  def ind(indent: Int) = "   +" * indent
}

sealed trait Variable {

  def prettyPrint(indent: Int = 0): String

  def reference: Ast.expr

  def expression: Ast.expr

  def statement: Ast.stmt

}

case class Constant(expression: Ast.expr) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} Const: $expression"
  }

  override def statement: stmt = Ast.stmt.Expr(expression)

  override def reference: expr = expression
}

case class NamedObservable(name: String) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} NamedObservable: $name"
  }

  override def statement: stmt = Ast.stmt.Expr(reference)

  override def expression: expr = reference

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)
}

case class Observable(name: String, expression: Ast.expr, dependsOn: Seq[Variable]) extends Variable {

  override def toString = prettyPrint(0)

  override def prettyPrint(indent: Int): String = {
    s"""
       |${ind(indent)} $name = $expression${dependsOn.map(_.prettyPrint(indent + 1)).mkString("")}""".stripMargin
  }

  override def statement: stmt = Ast.stmt.Assign(Seq(reference), expression)

  override def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)
}

case class FlatVariable(name: String, expression: Ast.expr, dependsOn: Seq[NamedObservable]) {

  override def toString = prettyPrint(0)

  private def prettyPrint(indent: Int): String = {
    s"""
       |${ind(indent)} $name = $expression${dependsOn.map(_.prettyPrint(indent + 1)).mkString("")}""".stripMargin
  }

  def statement: stmt = Ast.stmt.Assign(Seq(reference), expression)

  def reference: expr = Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load)
}
