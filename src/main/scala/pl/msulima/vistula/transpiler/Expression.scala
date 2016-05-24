package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template
import pl.msulima.vistula.util.ToArray

object Expression {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.AssignStmt(Ast.identifier(name), value) =>
      s"const $name = ${Transpiler(value)}"
    case Ast.stmt.Expr(value) =>
      val fragment = parseExpression(value)
      if (fragment.dependencies.isEmpty) {
        s"${fragment.code}"
      } else if (fragment.dependencies.size == 1) {
        s"""${Transpiler(fragment.dependencies.head)}.${fragment.mapper}($$arg => (${fragment.code}))""".stripMargin
      } else {
        s"""vistula.zip(${ToArray(Transpiler(fragment.dependencies))}).${fragment.mapper}($$args => (${fragment.code}))""".stripMargin
      }
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    Generator.apply.orElse(Attribute.apply).orElse(template.transpiler.Expression.apply).orElse(Primitives.apply)
      .orElse(parseSimpleExpression).orElse(parseLambda)
  }

  private lazy val parseSimpleExpression: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => Fragment(x)
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }

      Fragment(Seq(x, y), useFlatMap = false) {
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

      Fragment(Seq(x, y), useFlatMap = false) {
        case left :: right :: Nil =>
          s"$left $operator $right"
      }
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      val x: Seq[String] = args.map(arg => Transpiler(Ast.stmt.Expr(arg)))

      Fragment(s"$func(${x.mkString(", ")})", useFlatMap = true)
    case Ast.expr.Call(func, args, _, _, _) =>
      val x: Seq[String] = args.map(arg => Transpiler(Ast.stmt.Expr(arg)))

      Fragment(Seq(func), useFlatMap = true) {
        case f :: Nil =>
          s"$f(${x.mkString(", ")})"
      }
  }

  private lazy val parseLambda: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Lambda(Ast.arguments(args, None, None, Seq()), body) =>
      val argsNames = args.map({
        case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Param) =>
          x
      })
      val transpiledBody = Transpiler(Ast.stmt.Expr(body))

      Fragment(s"(${argsNames.mkString(", ")}) => $transpiledBody", useFlatMap = true)
  }
}
