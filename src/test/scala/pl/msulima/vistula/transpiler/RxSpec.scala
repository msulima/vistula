package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification

class RxSpec extends Specification {

  "transpiles reactive variables" in {
    Rx(Seq("x", "y"), "fnName", "body") must_==
      """
        |function fnName(x) {
        |  body
        |}
        |var __call0 = Zip([x, y]).flatMap(function (args) {
        |    return fnName(args[0]);
        |});""".stripMargin
  }
}
