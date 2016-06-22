package pl.msulima.vistula.transpiler.rpn

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula

class RpnSpec extends Specification {

  "transpiles generator" in {
    val program =
      """
        |A + 2;
        |A + 2 + B + False;
        |[1, 2 + 3, B, C - 4];
        |A.B;
      """.stripMargin

    Vistula.toJavaScriptRpn(program) must_==
      """A.rxMap($arg => ($arg + 2));
        |vistula.zip([A, B]).rxMap($args => ($args[0] + 2 + $args[1] + false));
        |vistula.constantObservable([
        |    vistula.constantObservable(1),
        |    vistula.constantObservable(2 + 3),
        |    B,
        |    C.rxMap($arg => ($arg - 4))
        |]);
        |A.rxFlatMap($arg => $arg.B);""".stripMargin
  }
}
