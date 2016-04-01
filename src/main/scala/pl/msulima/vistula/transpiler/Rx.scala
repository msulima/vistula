package pl.msulima.vistula.transpiler

object Rx {

  def map(variables: Seq[String], body: String) = {
    if (variables.isEmpty) {
      s"Observable($body)"
    } else if (variables.size == 1) {
      s"""${variables.head}.map(function (${variables.head}) {
         |  return $body;
         |})""".stripMargin
    } else {
      val vars = variables.mkString(", ")
      s"""Zip([$vars]).map(function (__args) {
          |  ${redefineVariables(variables)}
          |  return $body;
          |})""".stripMargin
    }
  }

  private def redefineVariables(variables: Seq[String]): String = {
    variables.zipWithIndex.map({
      case (variable, index) =>
        s"var $variable = __args[$index];"
    }).mkString(" ")
  }

  def call(func: String, arguments: Seq[String]) = {
    if (arguments.size == 1) {
      s"${arguments.head}.flatMap($func)"
    } else {
      val vars = arguments.mkString(", ")
      s"""Zip([$vars]).flatMap(function (__args) {
          |  return $func(${redefineArguments(arguments)});
          |});""".stripMargin
    }
  }

  private def redefineArguments(variables: Seq[String]): String = {
    variables.indices.map(index => {
      s"__args[$index]"
    }).mkString(", ")
  }
}
