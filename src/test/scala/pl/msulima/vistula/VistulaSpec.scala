package pl.msulima.vistula

import org.specs2.mutable.Specification

import scala.io.Source

class VistulaSpec extends Specification {

  private val HelloWorld =
    """
      |X = 42
      |W = X + 3 + a(b(Y), Z)
      |""".stripMargin

  "extract dependencies" in {
    val script = Vistula.toJavaScript(HelloWorld)
    println(script)
    script must not(beEmpty)
  }

  "transpiles clock" in {
    val script = Vistula.toJavaScript(Source.fromInputStream(getClass.getResourceAsStream("/clock.vst")).mkString)
    println(script)
    script must not(beEmpty)
  }
}
