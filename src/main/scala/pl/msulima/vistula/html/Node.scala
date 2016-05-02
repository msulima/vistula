package pl.msulima.vistula.html

import pl.msulima.vistula.parser.Ast

sealed trait Node

case class TextNode(textContent: String) extends Node

case class ObservableNode(expr: Ast.expr) extends Node

case class IfNode(test: Ast.expr, body: Seq[Node], elseBlock: Seq[Node]) extends Node

case class Element(tagName: String, childNodes: Seq[Node]) extends Node
