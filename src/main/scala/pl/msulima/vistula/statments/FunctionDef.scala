package pl.msulima.vistula.statments

import pl.msulima.vistula.Ast
import pl.msulima.vistula.Ast.stmt

object FunctionDef {

  val apply: PartialFunction[stmt, String] = {
    case stmt.FunctionDef(name: Ast.identifier, args: Ast.arguments, body: Seq[stmt], _) =>
      val argumentNames = args.args.map(arg => arg match {
        case Ast.expr.Name(id, _) =>
          id.name
      }).mkString(", ")

      s"""
         |function ${name.name}($argumentNames) {
         |  Rx.Observable.zip($argumentNames, function($argumentNames) {
         |    return ${Statement(body.head)}
         |  }
         |};""".stripMargin
  }
}
