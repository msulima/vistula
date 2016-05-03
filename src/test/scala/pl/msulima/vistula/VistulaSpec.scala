package pl.msulima.vistula

import java.io.File
import java.nio.file.Files

import org.specs2.mutable.Specification

import scala.collection.JavaConverters._
import scala.io.Source

class VistulaSpec extends Specification {

  private val HelloWorld =
    """
      |X = 42
      |W = X + 3 + a(b(Y), Z)
      | """.stripMargin

  "extract dependencies" in {
    val script = Vistula.toJavaScript(HelloWorld)
    println(script)
    script must not(beEmpty)
  }

  "transpiles clock" in {
    val script = Vistula.toJavaScript(Source.fromInputStream(getClass.getResourceAsStream("/clock.vst")).mkString)

    Files.write(new File("target/clock.js").toPath, script.split("\n").toSeq.asJava)
    script must not(beEmpty)
  }
}
