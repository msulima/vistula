package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

case object If {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      FunctionCall("vistula.ifStatement", Seq(
        Tokenizer.apply(testExpr),
        wrapScope(body.map(Tokenizer.applyStmt)),
        wrapScope(orElse.map(Tokenizer.applyStmt))
      ))
  }

  def applyExpr: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.IfExp(testExpr, body, orElse) =>
      FunctionCall("vistula.ifStatement", Seq(
        Tokenizer.apply(testExpr),
        wrapScope(Seq(Tokenizer.apply(body))),
        wrapScope(Seq(Tokenizer.apply(orElse)))
      ))
  }

  private def wrapScope(program: Seq[Token]): Token = {
    if (program.size == 1) {
      program.head
    } else {
      FunctionCall("vistula.wrap", Seq(FunctionDef.anonymous(program)))
    }
  }
}
