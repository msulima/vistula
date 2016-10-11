package pl.msulima.vistula

import java.io.File
import java.nio.file.Files

import org.specs2.mutable.Specification

import scala.collection.JavaConverters._
import scala.io.Source

class VistulaSpec extends Specification {

  "transpiles clock" in {
    val script = Vistula.toJavaScript(Source.fromInputStream(getClass.getResourceAsStream("/clock.vst")).mkString)

    Files.write(new File("target/clock.js").toPath, script.split("\n").toSeq.asJava)
    script must not(beEmpty)
  }

  "transpiles todo" in {
    val script = Vistula.toJavaScript(Source.fromInputStream(getClass.getResourceAsStream("/todo.vst")).mkString)

    Files.write(new File("target/todo.js").toPath, script.split("\n").toSeq.asJava)
    script must not(beEmpty)
  }

  "transpiles vistula" in {
    Vistula.browserify(Package("vistula"))
    Vistula.browserify(Package("stdlib"))
    Vistula.browserify(Package("js"))

    new File("target/vistula/modules/js.js").exists() must beTrue
    new File("target/vistula/modules/vistula.js").exists() must beTrue
    new File("target/vistula/modules/stdlib.js").exists() must beTrue
  }
}
