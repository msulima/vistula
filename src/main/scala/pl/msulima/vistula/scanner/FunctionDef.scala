package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object FunctionDef {

  def apply: PartialFunction[Ast.stmt, ScanResult] = {
    case stmt.FunctionDef(name, arguments, body, _) =>
      val argumentIds = arguments.args.map({
        case Ast.expr.Name(id, Ast.expr_context.Param) => id
      })
      ResultFunction(name, argumentIds, Scanner(body))
  }
}
