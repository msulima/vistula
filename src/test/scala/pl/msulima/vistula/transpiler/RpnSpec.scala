package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula

class RpnSpec extends Specification {

  "test" in {

    val program =
      """class M:
        |  def __init__(x: vistula.lang.Object, y: *vistula.lang.Object) {
        |    pass
        |  }
        |
        |const a = M(1, 2)
        |a.x + 1
        |a.y + 2
        |
        |let b = M(1, 2)
        |b.x + 1
        |b.y + 2
        |
      """.stripMargin

    Vistula.toJavaScript(program) must_==
      """function M(x, y) {
        |    this.x = x;
        |    this.y = y;
        |};
        |const a = new M(1, vistula.constantObservable(2));
        |a.x + 1;
        |a.y.rxMap($arg => ($arg + 2));
        |const b = vistula.constantObservable(new M(1, vistula.constantObservable(2)));
        |b.rxMap($arg => ($arg.x + 1));
        |b.rxFlatMap($arg => ($arg.y)).rxMap($arg => ($arg + 2));""".stripMargin
  }

  "transpiles generator" in {

    val program =
      """
        |A + 2;
        |A + 2 + B + False;
        |let A = vistula.Seq.apply(1, 2 + 3, B, C - 4)
        |A.B;
        |F(A, 3);
        |F(A, 3).B;
        |Y + 3 - a(Z + 1, 3);
      """.stripMargin

    Vistula.toJavaScript(program) must_==
      """A.rxMap($arg => ($arg + 2));
        |vistula.zip([
        |    A,
        |    B
        |]).rxMap($args => ($args[0] + 2 + $args[1] + false));
        |const A = vistula.Seq.apply(vistula.constantObservable(1), vistula.constantObservable(2 + 3), B, C.rxMap($arg => ($arg - 4)));
        |A.rxFlatMap($arg => ($arg.B));
        |F(A, vistula.constantObservable(3));
        |F(A, vistula.constantObservable(3)).rxFlatMap($arg => ($arg.B));
        |vistula.zip([
        |    Y,
        |    a(Z.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
        |]).rxMap($args => ($args[0] + 3 - $args[1]));""".stripMargin
  }
}
