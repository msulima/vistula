package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}

object Loop {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.For(Ast.expr.Name(argument, Ast.expr_context.Load), iterable, body) =>
      val program = body.init.map(Tokenizer.applyStmt) :+ Box(Tokenizer.applyStmt(body.last))
      val mapFunction = Reference(Tokenizer.apply(iterable), Ast.identifier("map"))

      FunctionCall(mapFunction, Seq(FunctionDef.anonymous(Variable(argument, ScopeElement.Default), program)))
  }
}
