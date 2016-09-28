package pl.msulima.vistula

import pl.msulima.vistula.parser.Ast

case class Package(path: Seq[Ast.identifier]) {

  def join = path.map(_.name).mkString(".")

  def toIdentifier = Ast.identifier(join)

  def resolve(child: String): Package = {
    resolve(Ast.identifier(child))
  }

  def resolve(child: Ast.identifier): Package = {
    copy(path = path :+ child)
  }

  override def toString: String = s"Package(${toIdentifier.name})"
}

object Package {

  val Root = Package(Seq())

  def apply(input: String) = {
    new Package(input.split("\\.").map(Ast.identifier))
  }
}

case class Class(`package`: Package, name: Ast.identifier)
