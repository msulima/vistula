package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.{ResultFunction, ScanResult}
import pl.msulima.vistula.util.Indent

object FunctionDef {

  def apply: PartialFunction[ScanResult, String] = {
    case ResultFunction(name, arguments, body) =>
      s"""function ${name.name}(${arguments.map(_.name).mkString(", ")}) {
          |${Indent.leftPad(Transpiler(body))}
          |}""".stripMargin
  }
}
