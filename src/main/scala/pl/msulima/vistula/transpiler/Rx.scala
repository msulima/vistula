package pl.msulima.vistula.transpiler

import pl.msulima.vistula.util.Indent

object Rx {

  def map(variables: Seq[String], body: String) = {
    transform(variables, body, "map")
  }

  def flatMap(variable: String, body: String) = {
    transform(Seq(variable), body, "flatMap")
  }

  private def transform(variables: Seq[String], body: String, operation: String) = {
    if (variables.isEmpty) {
      s"Observable($body)"
    } else if (variables.size == 1) {
      s"""${variables.head}.$operation(function (${variables.head}) {
         |${Indent.leftPad(body)}
         |})""".stripMargin
    } else {
      val vars = variables.mkString(", ")
      s"""Zip([$vars]).$operation(function (__args) {
          |  ${redefineVariables(variables)}
          |${Indent.leftPad(body)}
          |})""".stripMargin
    }
  }

  private def redefineVariables(variables: Seq[String]): String = {
    variables.zipWithIndex.map({
      case (variable, index) =>
        s"var $variable = __args[$index];"
    }).mkString(" ")
  }

}
