package pl.msulima.vistula.html

import fastparse.all._
import org.specs2.mutable.Specification


class TranspilerSpec extends Specification {

  def parse(code: String) = {
    (Parser.document ~ End).parse(code).get.value
  }

  "transpile" in {
    Transpiler(TestData.SampleHtml) must_==
      """vistula.dom.createElement(document.createElement("span"), [
        |  document.createTextNode("hello\n    is it "),
        |  vistula.dom.textObservable(lionel),
        |  document.createTextNode("?\n    "),
        |  vistula.dom.createElement(document.createElement("strong"), [
        |    document.createTextNode("you lookin'")
        |  ])
        |]);
        |vistula.dom.createElement(document.createElement("p"), [
        |  document.createTextNode("for?")
        |]);""".stripMargin
  }
}
