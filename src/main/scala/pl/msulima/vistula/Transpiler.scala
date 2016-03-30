package pl.msulima.vistula

import pl.msulima.vistula.statments.Expression

object Transpiler {

  def apply(program: Seq[Variable]) = {
    program.map(transpile).mkString("\n")
  }

  private def transpile(variable: Variable) = {
    s"var ${variable.identifier.name}=${sources(variable)}{return ${parseExpression(variable.expr)}});"
  }

  private def sources(variable: Variable) = {
    val names = variable.dependsOn.map(_.name)
    s"Rx.Observable.zip(${names.mkString(",")},function(${names.mkString(",")})"
  }

  val parseExpression: PartialFunction[Ast.expr, String] = Expression.parseExpression
}

case class Identifier(name: String)

case class Variable(identifier: Identifier, dependsOn: Set[Identifier], expr: Ast.expr)
