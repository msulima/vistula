package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Tokenizer extends App {

  def box(expr: Ast.expr): Token = {
    box(apply(expr))
  }

  def box(token: Token): Token = {
    token match {
      case MutableOperand(value) =>
        ConstantOperand(value)
      case _ =>
        val moved = findAndReplace(token)
        moved match {
          case ConstantOperation(RxMap(_), _) =>
            moved
          case _ =>
            ConstantOperation(Box, Seq(moved))
        }
    }
  }

  def apply2(expr: Ast.expr): Token = {
    findAndReplace(apply(expr))
  }

  def apply(expr: Ast.expr): Token = {
    BinOp.apply.orElse(Primitives.apply).orElse(Name.apply)(expr)
  }

  def findAndReplace(token: Token) = {
    val mutables = findMutables(token)

    if (mutables.isEmpty) {
      token
    } else {
      replaceMutables(token, mutables)
    }
  }

  def findMutables(token: Token): Seq[MutableOperand] = {
    token match {
      case x: MutableOperand =>
        Seq(x)
      case ConstantOperation(_, operands) =>
        operands.flatMap(findMutables)
      case _ =>
        Seq()
    }
  }

  def replaceMutables(token: Token, mutables: Seq[MutableOperand]): Token = {
    val mapping = if (mutables.size == 1) {
      Map(mutables.head -> "$arg")
    } else {
      mutables.zipWithIndex.map({
        case (mutable, index) => mutable -> s"$$args[$index]"
      }).toMap
    }

    ConstantOperation(RxMap(mutables), Seq(replaceMutables(token, mapping)))
  }

  def replaceMutables(token: Token, mapping: Map[MutableOperand, String]): Token = {
    token match {
      case x: MutableOperand =>
        ConstantOperand(mapping(x))
      case ConstantOperation(operation, operands) =>
        ConstantOperation(operation, operands.map(replaceMutables(_, mapping)))
      case x =>
        x
    }
  }
}
