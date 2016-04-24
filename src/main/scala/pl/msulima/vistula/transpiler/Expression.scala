package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Expression {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.Assign(Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load) +: _, value) =>
      s"var $name = ${Transpiler(Ast.stmt.Expr(value))};"
    case Ast.stmt.Expr(value) =>
      val fragment = parseExpression(value)
      if (fragment.dependencies.isEmpty) {
        s"${fragment.code}"
      } else {
        s"""Zip([${Transpiler(fragment.dependencies).mkString(",")}]).map(function ($$args) {
            |  return ${fragment.code};
            |})""".stripMargin
      }
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Num(x) => Fragment(s"ConstantObservable(${x.toString})")
    case Ast.expr.Str(x) => Fragment(s"""ConstantObservable("$x")""")

    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => Fragment(x)
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }

      Fragment(Seq(x, y)) {
        case left :: right :: Nil =>
          s"$left $operator $right"
      }
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      Fragment(Seq(x, y)) {
        case left :: right :: Nil =>
          s"$left $operator $right"
      }
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      val x: Seq[String] = args.map(arg => Transpiler(Ast.stmt.Expr(arg)))

      Fragment(s"$func(${x.mkString(", ")})")
  }
}
