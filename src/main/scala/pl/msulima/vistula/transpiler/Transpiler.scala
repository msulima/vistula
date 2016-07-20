package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.rpn.{Dereferencer, Token}

object Transpiler {

  def apply(token: Token): String = {
    toJavaScript(Dereferencer(Scope(Seq(), Seq()), token))
  }

  private def toJavaScript(token: Token): String = {
    rpn.Transpiler.toJavaScript(Seq(token)).dropRight(1)
  }
}
