package pl.msulima.vistula

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.ClassReference

case class Package(path: Seq[Ast.identifier]) {

  override def toString: String = s"Package(${toIdentifier.name})"

  def join = path.map(_.name).mkString(".")

  def toIdentifier = Ast.identifier(join)

  def resolve(child: String): Package = {
    resolve(Ast.identifier(child))
  }

  def resolve(child: Ast.identifier): Package = {
    copy(path = path :+ child)
  }

  def packageObjectReference: ClassReference = {
    ClassReference(this, Package.ModuleIdentifier)
  }

  def parent: Package = {
    Package(path.init)
  }

  def parents: Seq[Package] = {
    path.inits.toList.dropRight(2).map(Package.apply)
  }
}

object Package {

  val Root = Package(Seq())
  private val ModuleIdentifier = Ast.identifier("$Module")

  def apply(input: String) = {
    new Package(input.split("\\.").map(Ast.identifier))
  }
}

case class Class(`package`: Package, name: Ast.identifier)
