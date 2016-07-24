package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{Declare, FunctionCall, Reference}

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
      val identifierAcc = Ast.identifier("$acc")
      val identifierSource = Ast.identifier("$source")

      val declarations = Seq(
        Introduce(Variable(identifierAcc, Type(observable = false)), Constant("")), // FIXME function declaration
        Introduce(Variable(identifierSource, Type(observable = false)), Constant("")),
        Declare(acc, mutable = true, Reference(identifierAcc)),
        Declare(source, mutable = true, Reference(identifierSource))
      )

      FunctionCall(Reference(Ast.identifier("vistula.aggregate")), Seq(
        Tokenizer.boxed(initial),
        Reference(source),
        Observable(FunctionDef.anonymous(identifierAcc, identifierSource, FunctionScope(declarations, Seq(body))))
      ))
  }
}
