package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

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

object Generator {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.GeneratorExp(GeneratorBody(initial, body), GeneratorSource(acc, source)) =>
      Fragment(
        s"""vistula.aggregate(${Transpiler(initial)}, ${source.name}, ($$acc, $$source) => {
            |    const ${acc.name} = vistula.constantObservable($$acc);
            |    const ${source.name} = vistula.constantObservable($$source);
            |${Indent.leftPad("return " + Transpiler(body) + ";")}
            |})""".stripMargin, Seq())
  }
}
