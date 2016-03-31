package pl.msulima.vistula

import org.specs2.mutable.Specification

class VistulaSpec extends Specification {

  private val HelloWorld =
    """
      |def realTimeElapsed(elapsed):
      |  return clock - elapsed
      | """.stripMargin

  "extract dependencies" in {
    val script = Vistula.toJavaScript(HelloWorld)
    println(script)
    script must not(beEmpty)
  }
}
