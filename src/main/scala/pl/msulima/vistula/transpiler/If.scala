package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.{ResultIf, ScanResult}
import pl.msulima.vistula.util.Indent

object If {

  def apply: PartialFunction[ScanResult, String] = {
    case ResultIf(test, body, other) =>
      s"""${Transpiler.apply(test)}
         |return ${Rx.flatMap("__ifCondition", transpileBody(body, other))};""".stripMargin
  }

  private def transpileBody(body: Seq[ScanResult], other: Seq[ScanResult]) = {
    s"""if (__ifCondition) {
        |${Indent.leftPad(Transpiler.apply(body))}
        |} else {
        |${Indent.leftPad(Transpiler.apply(other))}
        |}""".stripMargin
  }
}
