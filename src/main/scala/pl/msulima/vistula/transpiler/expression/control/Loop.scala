package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}

object Loop {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.For(Ast.expr.Name(name, Ast.expr_context.Load), iterExpr, body, _) =>
      Loop(iterExpr, name, Observable(Transformer.returnLast(body)))
  }

  def apply(iterable: Ast.expr, argument: Ast.identifier, body: Token) = {
    val iter = Reference(Tokenizer.apply(iterable), Constant("map"))

    FunctionCall(iter, Seq(FunctionDef.anonymous(argument.name, body)))
  }
}
