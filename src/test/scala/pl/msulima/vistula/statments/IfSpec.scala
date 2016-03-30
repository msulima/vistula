package pl.msulima.vistula.statments

import org.specs2.mutable.Specification
import pl.msulima.vistula.Nesting
import pl.msulima.vistula.testutil.ToProgram

class IfSpec extends Specification {

  "transpiles if" in {
    val program =
      """
        |if y < 3:
        |  if y >= 0:
        |    y
        |  else:
        |    0
        |elif y > 0:
        |  wat
        |else:
        |  3
      """.stripMargin

    If.apply(Nesting(Seq(0)))(program.toStatement) must_==
      """var $_0_0 = null;
        |if (y < 3) {
        |  var $_0_0_0 = null;
        |  if (y >= 0) {
        |    $_0_0_0 = y;
        |  } else {
        |    $_0_0_0 = 0;
        |  }
        |  $_0_0 = $_0_0_0;
        |} else {
        |  $_0_0 = 3;
        |}
        |var $_0 = $_0_0;""".stripMargin
  }
}
