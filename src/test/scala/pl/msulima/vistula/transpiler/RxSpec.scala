package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification

class RxSpec extends Specification {

  "transpiles constants" in {
    Rx.map(Seq(), "42") must_==
      "ConstantObservable(42)"
  }

  "transpiles single reactive variable" in {
    Rx.map(Seq("Y"), "return Y + 2;") must_==
      """Y.map(function (Y) {
        |  return Y + 2;
        |})""".stripMargin
  }

  "transpiles reactive variables" in {
    Rx.map(Seq("Y", "Z"), "return Y + Z + 2;") must_==
      """Zip([Y, Z]).map(function (__args) {
        |  var Y = __args[0]; var Z = __args[1];
        |  return Y + Z + 2;
        |})""".stripMargin
  }

  "transpiles if with single argument" in {
    Rx.flatMap("X", "return X.map(identity);") must_==
      """X.flatMap(function (X) {
        |  return X.map(identity);
        |})""".stripMargin
  }
}
