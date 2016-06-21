package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.transpiler.{Transpiler, rpn}

object Vistula {

  def toJavaScript(input: String): String = {
    Transpiler(parse(input))
  }

  def toJavaScriptRpn(input: String): String = {
    parse(input).collect({
      case Ast.stmt.Expr(expr) => rpn.Transpiler.remaped(rpn.Tokenizer(expr).toList)
    }).mkString("", ";\n", ";")
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
