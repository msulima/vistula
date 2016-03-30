package pl.msulima.vistula.transpiler

import pl.msulima.vistula.Ast
import pl.msulima.vistula.Ast.stmt

object FunctionDef {

  val apply: PartialFunction[stmt, String] = {
    case Ast.stmt.FunctionDef(name: Ast.identifier, args: Ast.arguments, body: Seq[stmt], _) =>
      val argumentNames = args.args.map(arg => arg match {
        case Ast.expr.Name(id, _) =>
          id.name
      }).mkString(", ")

      s"""
         |function ${name.name}($argumentNames) {
         |  ${body.map(Statement.apply).mkString("\n")}
         |};""".stripMargin
    case Ast.stmt.Return(value) =>
      s"return ${value.map(Expression.parseExpression).getOrElse("")};"
  }
}
