package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._

trait BoxDereferencer {
  this: Dereferencer =>

  def boxDereferencer: PartialFunction[Token, Token] = {
    case Box(boxToken) =>
      unbox(boxToken)
  }

  private def unbox(token: Token): Token = {
    val inner = dereference(token)

    token match {
      case _: Box =>
        inner
      case _ =>
        inner match {
          case Observable(t: Constant) =>
            t
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
    }
  }
}
