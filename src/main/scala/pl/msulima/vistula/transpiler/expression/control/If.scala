package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

case object If {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      FunctionCall("vistula.ifStatement", Seq(
        Tokenizer.apply(testExpr),
        wrapScope(body),
        wrapScope(orElse)
      ))
  }

  private def wrapScope(program: Seq[Ast.stmt]): Token = {
    val body = program.map(Tokenizer.applyStmt)
    if (body.size == 1) {
      body.head
    } else {
      FunctionCall("vistula.wrap", Seq(FunctionDef.anonymous(body)))
    }
  }
}
