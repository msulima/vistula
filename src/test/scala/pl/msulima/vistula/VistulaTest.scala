package pl.msulima.vistula

import fastparse.all._
import org.specs2.mutable.Specification
import pl.msulima.vistula.Ast.stmt

object Vistula {

  def toJavaScript(input: String): String = {
    Transpiler(Vistula(input))
  }

  def apply(input: String): Seq[Variable] = {
    findVariables((Statements.file_input ~ End).parse(input).get.value).toList
  }

  def findVariables(program: Seq[stmt]): Seq[Variable] = {
    program.map(parseStatement)
  }

  private def parseStatement(statement: stmt) = statement match {
    case stmt.Assign(target +: _, expression) =>
      val targetId = parseTarget(target)
      val dependsOn = parseExpression(expression)

      Variable(targetId, dependsOn, expression)
  }

  private def parseTarget: PartialFunction[Ast.expr, Identifier] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) =>
      Identifier(x)
  }

  private def parseExpression(expression: Ast.expr): Set[Identifier] = expression match {
    case Ast.expr.Num(x) => Set.empty
    case Ast.expr.Str(x) => Set.empty
    case Ast.expr.BinOp(x, op, y) => (parseTarget.lift(x) ++ parseTarget.lift(y)).toSet
  }
}

class VistulaTest extends Specification {

  private val HelloWorld =
    """
      |hello = 5
      |world = hello + 2
      | """.stripMargin

  "extract dependencies" in {
    extractDependencies(Vistula(HelloWorld)) must_== Seq(
      ("hello", Set.empty),
      ("world", Set(Identifier("hello")))
    )
  }

  private def extractDependencies(variables: Seq[Variable]) = {
    variables.map(x => (x.identifier.name, x.dependsOn))
  }

  private val Timer =
    """
      |def max3(x):
      |  x + 3
      |
      |first = timer + 2
      |output = max3(first)
      | """.stripMargin

  "transpile" in {
    Vistula.toJavaScript(Timer) must_==
      """var output=Rx.Observable.zip(timer,function(timer){return timer+2;});""".stripMargin
  }
}

