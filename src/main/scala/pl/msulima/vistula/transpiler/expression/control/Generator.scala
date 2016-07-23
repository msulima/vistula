package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
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

case object Generator extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.GeneratorExp(GeneratorBody(initial, body), GeneratorSource(acc, source)) =>
      Operation(Generator,
        Seq(Constant(source.name), Constant(acc.name), Tokenizer.boxed(initial)),
        Observable(FunctionScope(Seq(body)))
      )
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val source = operands.head.value
    val acc = operands(1).value
    val initial = operands(2).value

    // FIXME move assignments to inputs
    Constant(
      s"""vistula.aggregate($initial, $source, ($$acc, $$source) => {
          |    const $acc = vistula.constantObservable($$acc);
          |    const $source = vistula.constantObservable($$source);
          |${Indent.leftPad(output.value)}
          |})""".stripMargin)
  }
}
