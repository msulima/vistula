package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template
import pl.msulima.vistula.transpiler.expression.{Arithmetic, FunctionCall, Lambda}
import pl.msulima.vistula.util.ToArray

object Expression {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.DeclareStmt(Ast.identifier(name), value, _) =>
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

  lazy val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    Generator.apply.orElse(Attribute.apply).orElse(template.transpiler.Expression.apply).orElse(Primitives.apply)
      .orElse(loadName).orElse(FunctionCall.apply).orElse(Arithmetic.apply).orElse(Lambda.apply)
  }

  private lazy val loadName: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) =>
      Fragment(x)
  }
}
