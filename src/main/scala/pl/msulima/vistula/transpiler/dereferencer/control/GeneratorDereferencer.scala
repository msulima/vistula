package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference
import pl.msulima.vistula.transpiler.dereferencer.reference.FunctionCallDereferencer
import pl.msulima.vistula.transpiler.scope.{Scope, ScopeElement, Variable}

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

trait GeneratorDereferencer {
  this: Dereferencer with FunctionCallDereferencer with FunctionDereferencer =>

  def generatorDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.GeneratorExp(GeneratorBody(initial, body), GeneratorSource(acc, source)))) =>
      val arguments = Seq(
        Variable(acc, ScopeElement.DefaultConst),
        Variable(source, ScopeElement.DefaultConst)
      )
      val transpiledBody = Box(Tokenizer.applyStmt(body))
      val innerBody = anonymousFunction(arguments, Seq(transpiledBody))

      functionCall(Reference(Reference(Scope.VistulaHelper), Ast.identifier("aggregate")), Seq(
        dereference(Reference(source)),
        dereference(initial),
        innerBody
      ))
  }
}
