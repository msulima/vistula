package pl.msulima.vistula.template.parser

import pl.msulima.vistula.parser.Ast

sealed trait Node

sealed trait Attribute

case class TextNode(textContent: String) extends Node

case class ObservableNode(expr: Ast.expr) extends Node

case class IfNode(test: Ast.expr, body: Seq[Node], elseBlock: Seq[Node]) extends Node

case class ForNode(identifier: Ast.identifier, expression: Ast.expr, body: Seq[Node]) extends Node

case class AttributeMarker(name: String) extends Attribute

case class AttributeValue(name: String, value: Ast.expr) extends Attribute

case class AttributeEvent(name: String, value: Ast.expr) extends Attribute

case class Tag(name: String, id: Option[Ast.identifier], attributes: Seq[Attribute]) extends Node

case class Element(tag: Tag, childNodes: Seq[Node]) extends Node
