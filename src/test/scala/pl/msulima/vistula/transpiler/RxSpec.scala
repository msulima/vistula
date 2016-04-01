package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification

class RxSpec extends Specification {

  "transpiles constants" in {
    Rx.map(Seq(), "42") must_==
      "Observable(42)"
  }

  "transpiles single reactive variable" in {
    Rx.map(Seq("Y"), "Y + 2") must_==
      """Y.map(function (Y) {
        |  return Y + 2;
        |})""".stripMargin
  }

  "transpiles reactive variables" in {
    Rx.map(Seq("Y", "Z"), "Y + Z + 2") must_==
      """Zip([Y, Z]).map(function (__args) {
        |  var Y = __args[0]; var Z = __args[1];
        |  return Y + Z + 2;
        |})""".stripMargin
  }

  "transpiles function call with single argument" in {
    Rx.call("A", Seq("Y")) must_==
      "Y.flatMap(A)"
  }

  "transpiles function calls" in {
    Rx.call("A", Seq("Y", "Z")) must_==
      """Zip([Y, Z]).flatMap(function (__args) {
        |  return A(__args[0], __args[1]);
        |});""".stripMargin
  }
}
