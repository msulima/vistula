package pl.msulima.vistula

import org.specs2.mutable.Specification

class VistulaSpec extends Specification {

  private val HelloWorld =
    """
      |W = X + 3 + a(b(Y), Z)
      |""".stripMargin

  "extract dependencies" in {
    val script = Vistula.toJavaScript(HelloWorld)
    println(script)
    script must not(beEmpty)
  }
}
