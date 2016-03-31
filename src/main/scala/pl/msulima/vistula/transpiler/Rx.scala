package pl.msulima.vistula.transpiler

object Rx {

  def call(func: String, arguments: Seq[String]) = {
    val vars = arguments.mkString(", ")
    s"""Zip([$vars]).flatMap(function (__args) {
        |  return $func(${redefine(arguments)});
        |});""".stripMargin
  }

  def apply(variables: Seq[String], func: String, body: String) = {
    val vars = variables.mkString(", ")
    s"""
       |function __$func($vars) {
       |  $body
       |}
       |var $func = Zip($vars).flatMap(function (__args) {
       |  return __$func(${redefine(variables)});
       |});""".stripMargin
  }

  private def redefine(variables: Seq[String]): String = {
    variables.indices.map(index => {
      s"__args[$index]"
    }).mkString(", ")
  }
}
