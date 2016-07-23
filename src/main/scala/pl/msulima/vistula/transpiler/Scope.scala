package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

case class ScopedResult(scope: Scope, program: Seq[Token])

case class Scope(variables: Seq[Ast.identifier], observables: Seq[Ast.identifier])
