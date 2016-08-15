package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}

object GeneratorBody {

  def unapply(expr: Ast.expr): Option[(Ast.stmt, Ast.stmt)] = expr match {
    case Ast.expr.BoolOp(Ast.boolop.Or, initial +: body +: _) =>
      Some((Ast.stmt.Expr(initial), Ast.stmt.Expr(body)))
    case _ => None
  }
}

object GeneratorSource {

  def unapply(expr: Seq[Ast.comprehension]): Option[(Ast.identifier, Ast.identifier)] = expr match {
    case Ast.comprehension(Ast.expr.Name(acc, Ast.expr_context.Load), Ast.expr.Name(source, Ast.expr_context.Load), _) +: _ =>
      Some((acc, source))
    case _ => None
  }
}

case object Generator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.GeneratorExp(GeneratorBody(initial, body), GeneratorSource(acc, source)) =>
      val arguments = Seq(
        Variable(acc, ScopeElement.DefaultConst),
        Variable(source, ScopeElement.DefaultConst)
      )
      val innerBody = FunctionDef.anonymous(arguments, Seq(Tokenizer.applyStmt(body)))

      FunctionCall("vistula.aggregate", Seq(
        Reference(source),
        Tokenizer.applyStmt(initial),
        innerBody
      ))
  }
}
