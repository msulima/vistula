package pl.msulima.vistula.html

import fastparse.all._
import org.specs2.mutable.Specification
import pl.msulima.vistula.parser.Ast

object TestData {

  val SampleHtml =
    """
      |<span>
      |    hello
      |    is it {{ lionel }}?
      |    <strong>you lookin'</strong>
      |</span>
      |<p>for?</p>
      | """.stripMargin

}

class ParserSpec extends Specification {

  private def parse(code: String) = {
    (Statements.document ~ End).parse(code).get.value
  }

  "parse" in {
    parse(TestData.SampleHtml) must_== Seq(
      Element("span", Seq(
        TextNode("hello\n    is it "),
        ObservableNode(Ast.identifier("lionel")),
        TextNode("?\n    "),
        Element("strong", Seq(
          TextNode("you lookin'")
        ))
      )),
      Element("p", Seq(
        TextNode("for?")
      ))
    )
  }
}
