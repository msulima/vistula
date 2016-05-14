package pl.msulima.vistula.html

import pl.msulima.vistula.parser.Ast

sealed trait Node

case class TextNode(textContent: String) extends Node

case class ObservableNode(expr: Ast.expr) extends Node

case class IfNode(test: Ast.expr, body: Seq[Node], elseBlock: Seq[Node]) extends Node

case class ForNode(identifier: Ast.identifier, expression: Ast.expr, body: Seq[Node]) extends Node

case class Attribute(name: String, value: Option[Ast.expr])

case class Tag(name: String, attributes: Seq[Attribute]) extends Node

case class Element(tag: Tag, childNodes: Seq[Node]) extends Node
