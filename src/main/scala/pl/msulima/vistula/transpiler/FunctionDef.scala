package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.{ResultFunction, ScanResult}
import pl.msulima.vistula.util.Indent

object FunctionDef {

  def apply: PartialFunction[ScanResult, String] = {
    case ResultFunction(name, arguments, body) =>
      s"""function ${name.name}(${arguments.map(_.name).mkString(", ")}) {
         |${Indent.leftPad(Transpiler(body).mkString("\n"))}
         |}""".stripMargin
    //      val argumentNames = args.args.map(arg => arg match {
    //        case Ast.expr.Name(id, _) =>
    //          id.name
    //      }).mkString(", ")
    //
    //      s"""
    //         |function ${name.name}($argumentNames) {
    //         |  ${body.map(Statement.apply).mkString("\n")}
    //         |};""".stripMargin
    //    case Ast.stmt.Return(value) =>
    //      s"return ${value.map(Expression.parseExpression).getOrElse("")};"
  }
}
