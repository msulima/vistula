package pl.msulima.vistula

import pl.msulima.vistula.parser.Ast

case class Package(path: Seq[Ast.identifier]) {

  def join = path.map(_.name).mkString(".")

  def resolve(child: String): Package = {
    copy(path = path :+ Ast.identifier(child))
  }
}

object Package {

  val Root = Package(Seq())

  def apply(input: String) = {
    new Package(input.split("\\.").map(Ast.identifier))
  }
}

case class Class(`package`: Package, name: Ast.identifier)
