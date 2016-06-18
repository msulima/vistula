package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class Scope(variables: Seq[Ast.identifier], observables: Seq[Ast.identifier], parent: Scope)
