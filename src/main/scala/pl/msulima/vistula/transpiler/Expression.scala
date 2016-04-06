package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.scanner.{FlatVariable, ResultVariable, ScanResult}

object Expression {

  def apply: PartialFunction[ScanResult, String] = {
    case ResultVariable(variables) =>
      variables.map(parseFlatVariable).mkString("\n")
  }

  private lazy val parseFlatVariable: PartialFunction[FlatVariable, String] = {
    case FlatVariable(target, value: Ast.expr.Call, dependsOn) =>
      s"${toTarget(target)} ${parseExpression(value)};"
    case FlatVariable(target, Ast.expr.Name(variable, Ast.expr_context.Load), Nil) =>
      s"${toTarget(target)} ${variable.name};"
    case FlatVariable(target, value, Nil) =>
      s"${toTarget(target)} ConstantObservable(${parseExpression(value)});"
    case FlatVariable(target, value, dependsOn) =>
      s"${toTarget(target)} ${Rx.map(dependsOn.map(_.name.name), s"return ${parseExpression(value)};")};"
  }

  private def toTarget(id: Option[Ast.identifier]) = id match {
    case Some(Ast.identifier(name)) => s"var $name ="
    case None => "return"
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, String] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => x
    case Ast.expr.Num(x) => x.toString
    case Ast.expr.Str(x) => "\"" + x + "\""
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }
      s"${parseExpression(x)} $operator ${parseExpression(y)}"
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }
      s"${parseExpression(x)} $operator ${parseExpression(y)}"
    case Ast.expr.Call(func, args, _, _, _) =>
      s"${parseExpression(func)}(${args.map(parseExpression).mkString(", ")})"
  }
}
