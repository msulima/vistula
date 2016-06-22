package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Tokenizer extends App {

  def box(expr: Ast.expr): Token = {
    Operation(Box, Seq(apply(expr)))
  }

  def box(token: Token): Token = {
    Operation(Box, Seq(token))
  }

  def apply2(expr: Ast.expr): Token = {
    findAndReplace(apply(expr))
  }

  def apply(expr: Ast.expr): Token = {
    BinOp.apply.orElse(Primitives.apply).orElse(FunctionCall.apply).orElse(Name.apply)(expr)
  }

  def findAndReplace(token: Token): Token = {
    val mutables = findMutables(token)

    if (mutables.isEmpty) {
      token
    } else {
      replaceMutables(token, mutables)
    }
  }

  def findMutables(token: Token): Seq[Reference] = {
    token match {
      case x: Reference =>
        Seq(x)
      case Operation(Box, _) =>
        Seq()
      case Operation(_, operands) =>
        operands.flatMap(findMutables)
      case _ =>
        Seq()
    }
  }

  def replaceMutables(token: Token, mutables: Seq[Reference]): Token = {
    val mapping = if (mutables.size == 1) {
      Map(mutables.head -> "$arg")
    } else {
      mutables.zipWithIndex.map({
        case (mutable, index) => mutable -> s"$$args[$index]"
      }).toMap
    }

    Operation(RxMap(mutables), Seq(replaceMutables(token, mapping)))
  }

  def replaceMutables(token: Token, mapping: Map[Reference, String]): Token = {
    token match {
      case x: Reference =>
        Constant(mapping(x))
      case Operation(operation, operands) =>
        Operation(operation, operands.map(replaceMutables(_, mapping)))
      case x =>
        x
    }
  }
}
