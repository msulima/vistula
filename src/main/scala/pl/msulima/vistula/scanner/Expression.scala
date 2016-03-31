package pl.msulima.vistula.scanner

import java.util.concurrent.atomic.AtomicInteger

import pl.msulima.vistula.parser.Ast

object Expression {

  def apply: PartialFunction[Ast.stmt, Set[String]] = {
    case Ast.stmt.Expr(value) =>
      Set.empty
  }

  def apply2: PartialFunction[Ast.stmt, Variable] = {
    apply3(VariableCounter())
  }

  private def apply3(counter: VariableCounter): PartialFunction[Ast.stmt, Variable] = {
    case Ast.stmt.Assign(Ast.expr.Name(Ast.identifier(target), Ast.expr_context.Load) +: _, value) =>
      val valueVar = parseExpression(value)
      Observable(target, valueVar.expression, valueVar.dependencies)
  }

  lazy val parseExpression: PartialFunction[Ast.expr, Variable] = {
    case expr@Ast.expr.Num(x) => Constant(expr)
    case expr@Ast.expr.Str(x) => Constant(expr)
    case expr@Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => NamedObservable(x)

    case Ast.expr.BinOp(left, op, right) =>
      val leftVar = parseExpression(left)
      val rightVar = parseExpression(right)

      Observable(VariableCounter.next(), Ast.expr.BinOp(leftVar.reference, op, rightVar.reference), Set(leftVar, rightVar))
    case Ast.expr.Compare(left, ops, right +: _) =>
      val leftVar = parseExpression(left)
      val rightVar = parseExpression(right)

      Observable(VariableCounter.next(), Ast.expr.Compare(leftVar.reference, ops, Seq(rightVar.reference)), Set(leftVar, rightVar))
    case expr@Ast.expr.Call(Ast.expr.Name(Ast.identifier(target), Ast.expr_context.Load), args, _, _, _) =>
      val argsVars = args.map(parseExpression)

      Observable(VariableCounter.next(), expr.copy(args = argsVars.map(_.reference)), argsVars.toSet)
  }
}

object VariableCounter {

  private val x = new AtomicInteger()

  def next() = "T" + x.incrementAndGet()
}

case class VariableCounter(id: Int = 1) {
  def next = copy(id = id + 1)

  override def toString = "T" + id
}
