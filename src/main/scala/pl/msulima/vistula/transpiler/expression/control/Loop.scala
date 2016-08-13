package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}

object Loop {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.For(Ast.expr.Name(name, Ast.expr_context.Load), iterExpr, body, _) =>
      Loop(iterExpr, name, body.map(Tokenizer.applyStmt))
  }

  def apply(iterable: Ast.expr, argument: Ast.identifier, body: Seq[Token]) = {
    val iter = Reference(Tokenizer.apply(iterable), Constant("map"))

    FunctionCall(iter, Seq(FunctionDef.anonymous(Variable(argument, ScopeElement.Default), body)))
  }
}
