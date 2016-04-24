package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object FunctionDef {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) => id
      })
      s"""function ${name.name}(${argumentIds.map(_.name).mkString(", ")}) {
          |${Indent.leftPad(Transpiler.returnLast(body))}
          |}""".stripMargin
  }
}
