package pl.msulima.vistula.scanner

import java.util.concurrent.atomic.AtomicInteger

import pl.msulima.vistula.parser.Ast

object Expression {

  def apply: PartialFunction[Ast.stmt, ResultVariable] = {
    case Ast.stmt.Assign(Ast.expr.Name(target, Ast.expr_context.Load) +: _, value) =>
      parseSingleExpression(Some(target), value)
    case Ast.stmt.Expr(value) =>
      parseSingleExpression(None, value)
  }

  private def parseSingleExpression(target: Option[Ast.identifier], value: Ast.expr) = {
    val result = if (parseStatic.isDefinedAt(value)) {
      Observable(target, value, Seq())
    } else {
      val (expr, variables) = parseDynamic(new VariableCounter(target.getOrElse(Ast.identifier("Temp"))))(value)
      Observable(target, expr, variables)
    }

    ResultVariable(Flatter(result))
  }

  private def parseExpression(c: VariableCounter): PartialFunction[Ast.expr, Variable] = {
    val name = c.next()
    parseStatic.orElse(parseDynamic(c).andThen({
      case (expr, variables) =>
        Observable(Some(name), expr, variables)
    }))
  }

  private lazy val parseStatic: PartialFunction[Ast.expr, Variable] = {
    case expr: Ast.expr.Num => Constant(expr)
    case expr: Ast.expr.Str => Constant(expr)
    case expr@Ast.expr.Name(x, Ast.expr_context.Load) => NamedObservable(x)
  }

  private def parseDynamic(c: VariableCounter): PartialFunction[Ast.expr, (Ast.expr, Seq[Variable])] = {
    case Ast.expr.BinOp(left, op, right) =>
      val leftVar = parseExpression(c)(left)
      val rightVar = parseExpression(c)(right)

      Ast.expr.BinOp(leftVar.reference, op, rightVar.reference) -> Seq(leftVar, rightVar)
    case Ast.expr.Compare(left, ops, right +: _) =>
      val leftVar = parseExpression(c)(left)
      val rightVar = parseExpression(c)(right)

      Ast.expr.Compare(leftVar.reference, ops, Seq(rightVar.reference)) -> Seq(leftVar, rightVar)
    case expr@Ast.expr.Call(Ast.expr.Name(target, Ast.expr_context.Load), args, _, _, _) =>
      val argsVars = args.map(parseExpression(c))

      expr.copy(args = argsVars.map(_.reference)) -> argsVars
  }
}

class VariableCounter(base: Ast.identifier) {

  private val x = new AtomicInteger()

  def next() = {
    val id = x.incrementAndGet()
    Ast.identifier("__" + base.name + "_" + id)
  }
}
