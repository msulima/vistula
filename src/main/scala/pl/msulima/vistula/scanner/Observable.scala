package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr
import pl.msulima.vistula.util.Indent.ind


sealed trait Variable {

  override def toString = prettyPrint(0)

  def prettyPrint(indent: Int = 0): String

  def reference: Ast.expr

  def expression: Ast.expr

}

case class Constant(expression: Ast.expr) extends Variable {

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} Const: $expression"
  }

  override def reference: expr = expression
}

case class NamedObservable(name: Ast.identifier) extends Variable {

  override def prettyPrint(indent: Int): String = {
    s"\n${ind(indent)} NamedObservable: $name"
  }

  override def expression: expr = reference

  override def reference: expr = Ast.expr.Name(name, Ast.expr_context.Load)
}

case class Observable(name: Ast.identifier, expression: Ast.expr, dependsOn: Seq[Variable]) extends Variable {

  override def prettyPrint(indent: Int): String = {
    s"""
       |${ind(indent)} $name = $expression${dependsOn.map(_.prettyPrint(indent + 1)).mkString("")}""".stripMargin
  }

  override def reference: expr = Ast.expr.Name(name, Ast.expr_context.Load)
}
