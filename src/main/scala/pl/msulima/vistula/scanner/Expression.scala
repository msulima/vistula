package pl.msulima.vistula.scanner

import java.util.concurrent.atomic.AtomicInteger

import pl.msulima.vistula.parser.Ast

object Expression {

  def apply: PartialFunction[Ast.stmt, Set[String]] = {
    case Ast.stmt.Expr(value) =>
      Set.empty
  }

  def apply2: PartialFunction[Ast.stmt, Seq[Variable]] = {
    case Ast.stmt.Assign(Ast.expr.Name(Ast.identifier(target), Ast.expr_context.Load) +: _, value) =>
      val valueVars = parseExpression(new VariableCounter(target))(value)

      (Observable(target, valueVars.head.expression) +: valueVars.tail).reverse
  }

  private lazy val parseStatic: PartialFunction[Ast.expr, Seq[Variable]] = {
    case expr@Ast.expr.Num(x) => Seq(Constant(expr))
    case expr@Ast.expr.Str(x) => Seq(Constant(expr))
    case expr@Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => Seq(NamedObservable(x))
  }

  private def parseExpression(c: VariableCounter): PartialFunction[Ast.expr, Seq[Variable]] = {
    parseStatic.orElse(parseDynamic(c))
  }

  private def parseDynamic(c: VariableCounter): PartialFunction[Ast.expr, Seq[Variable]] = {
    case Ast.expr.BinOp(left, op, right) =>
      val leftVar = parseExpression(c)(left)
      val rightVar = parseExpression(c)(right)

      Observable(c.next(), Ast.expr.BinOp(leftVar.head.reference, op, rightVar.head.reference)) +: (leftVar ++ rightVar)
    case Ast.expr.Compare(left, ops, right +: _) =>
      val leftVar = parseExpression(c)(left)
      val rightVar = parseExpression(c)(right)

      Observable(c.next(), Ast.expr.Compare(leftVar.head.reference, ops, Seq(rightVar.head.reference))) +: (leftVar ++ rightVar)
    case expr@Ast.expr.Call(Ast.expr.Name(Ast.identifier(target), Ast.expr_context.Load), args, _, _, _) =>
      val argsVars = args.map(parseExpression(c))

      Observable(c.next(), expr.copy(args = argsVars.map(_.head.reference))) +: argsVars.flatten
  }
}

class VariableCounter(base: String) {

  private val x = new AtomicInteger()

  def next() = {
    "__" + base + "_" + x.incrementAndGet()
  }
}
