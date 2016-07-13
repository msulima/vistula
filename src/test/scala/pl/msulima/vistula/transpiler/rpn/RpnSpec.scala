package pl.msulima.vistula.transpiler.rpn

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula

class RpnSpec extends Specification {

  "test" in {

    val program =
      """A = X + 2
        |B = False
        |C.D = 2 + 3
        | """.stripMargin

    Vistula.toJavaScriptRpn(program) must_==
      """X.rxMap($arg => ($arg + 2)).rxForEachOnce($arg => A.rxPush($arg));
        |vistula.constantObservable(false).rxForEachOnce($arg => B.rxPush($arg));
        |vistula.constantObservable(2 + 3).rxForEachOnce($arg => C.rxFlatMap($arg => ($arg.D)).rxPush($arg));""".stripMargin
  }

  "transpiles generator" in {

    val program =
      """
        |A + 2;
        |A + 2 + B + False;
        |[1, 2 + 3, B, C - 4];
        |A.B;
        |F(A, 3);
        |F(A, 3).B;
        |Y + 3 - a(Z + 1, 3);
      """.stripMargin

    Vistula.toJavaScriptRpn(program) must_==
      """A.rxMap($arg => ($arg + 2));
        |vistula.zip([
        |    A,
        |    B
        |]).rxMap($args => ($args[0] + 2 + $args[1] + false));
        |vistula.constantObservable([
        |    vistula.constantObservable(1),
        |    vistula.constantObservable(2 + 3),
        |    B,
        |    C.rxMap($arg => ($arg - 4))
        |]);
        |A.rxFlatMap($arg => ($arg.B));
        |F(A, vistula.constantObservable(3));
        |F(A, vistula.constantObservable(3)).rxFlatMap($arg => ($arg.B));
        |vistula.zip([
        |    Y,
        |    a(Z.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
        |]).rxMap($args => ($args[0] + 3 - $args[1]));""".stripMargin
  }
}
