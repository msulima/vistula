package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.function.FunctionDereferencer
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, FunctionCallDereferencer}
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.Scope

trait IfDereferencer {
  this: Dereferencer with FunctionCallDereferencer with FunctionDereferencer =>

  private val IfStatement = Reference(Reference(Scope.VistulaHelper), Ast.identifier("ifStatement"))
  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))

  def ifDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.If(testExpr, body, orElse)) =>
      dereferenceIf(testExpr, body.map(Tokenizer.applyStmt), orElse.map(Tokenizer.applyStmt))
    case Direct(Ast.stmt.Expr(Ast.expr.IfExp(testExpr, body, orElse))) =>
      dereferenceIf(testExpr, Seq(Tokenizer.apply(body)), Seq(Tokenizer.apply(orElse)))
  }

  private def dereferenceIf(testExpr: expr, tokenizedBody: Seq[Token], tokenizedOrElse: Seq[Token]): Expression = {
    functionCall(IfStatement, Seq(
      dereference(Tokenizer.apply(testExpr)),
      wrapScope(tokenizedBody),
      wrapScope(tokenizedOrElse)
    ))
  }

  private def wrapScope(program: Seq[Token]): Expression = {
    if (program.size == 1) {
      dereference(program.head)
    } else {
      functionCall(Wrap, Seq(anonymousFunction(Seq(), program)))
    }
  }
}
