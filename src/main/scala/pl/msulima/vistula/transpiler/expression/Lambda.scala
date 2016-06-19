package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, RxFlatMap, Transpiler}

object Lambda {

  def apply: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Lambda(Ast.arguments(args, None, None, Seq()), body) =>
      val argsNames = args.map({
        case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Param) =>
          x
      })
      val transpiledBody = Transpiler(Ast.stmt.Expr(body))

      CodeTemplate(s"(${argsNames.mkString(", ")}) => $transpiledBody", RxFlatMap)
  }
}
