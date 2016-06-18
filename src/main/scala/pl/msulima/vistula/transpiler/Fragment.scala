package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

sealed trait Mapper

case object Static extends Mapper

case object RxMap extends Mapper

case object RxFlatMap extends Mapper

case class Fragment(template: String, mapper: Mapper, dependencies: Seq[Ast.expr] = Seq())
