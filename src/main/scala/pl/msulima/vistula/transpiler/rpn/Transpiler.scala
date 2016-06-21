package pl.msulima.vistula.transpiler.rpn

import scala.collection.mutable

object Transpiler extends App {

  def remaped(tokens: List[Token]): String = {
    apply(remap(tokens))
  }

  private def remap(tokens: List[Token]): List[Token] = {
    moveMutables(tokens).toList
  }

  def box(tokens: Seq[Token]): Seq[Token] = {
    tokens.toList match {
      case MutableOperand(value) :: Nil =>
        Seq(ConstantOperand(value))
      case _ =>
        val moved = moveMutables(tokens)
        if (moved.last.isInstanceOf[RxMap]) {
          moved
        } else {
          moved :+ Box
        }
    }
  }

  private def moveMutables(tokens: Seq[Token]): Seq[Token] = {
    val mutablesCount = tokens.count(_.isInstanceOf[MutableOperand])

    if (mutablesCount == 0) {
      tokens
    } else if (mutablesCount == 1 && tokens.size == 1) {
      tokens.map(x => ConstantOperand(x.asInstanceOf[MutableOperand].value))
    } else {
      val (mutables, constants) = mapMutables(mutablesCount, tokens)

      mutables.map(x => ConstantOperand(x.value)) ++ constants :+ RxMap(mutables.size)
    }
  }

  private def mapMutables(mutablesCount: Int, tokens: Seq[Token]) = {
    var mutables = mutable.ArrayBuffer[MutableOperand]()

    val constants = if (mutablesCount == 1) {
      tokens.map({
        case operand: MutableOperand =>
          mutables += operand
          ConstantOperand("$arg")
        case x => x
      })
    } else {
      tokens.map({
        case operand: MutableOperand =>
          val index = mutables.size
          mutables += operand
          ConstantOperand(s"$$args[$index]")
        case x => x
      })
    }

    (mutables.toList, constants)
  }

  def apply(tokens: List[Token]): String = {
    var stack = mutable.ArrayBuffer[ConstantOperand]()

    for (token <- tokens) {
      token match {
        case operand: ConstantOperand =>
          stack += operand
        case operator: ConstantOperator =>
          val (left, right) = stack.splitAt(stack.size - operator.operands)
          stack = left :+ operator(right.toList)
      }
    }

    stack.last.value
  }
}
