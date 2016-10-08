package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference
import pl.msulima.vistula.transpiler.dereferencer.reference.FunctionCallDereferencer
import pl.msulima.vistula.transpiler.scope.Scope
import pl.msulima.vistula.util.Indent

trait IfDereferencer {
  this: Dereferencer with FunctionCallDereferencer with FunctionDereferencer =>

  private val IfStatement = Reference(Reference(Scope.VistulaHelper), Ast.identifier("ifStatement"))

  def ifDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.If(testExpr, body, orElse)) =>
      dereferenceIf(testExpr, body.map(Tokenizer.applyStmt), orElse.map(Tokenizer.applyStmt))
    case Direct(Ast.stmt.Expr(Ast.expr.IfExp(testExpr, body, orElse))) =>
      dereferenceIf(testExpr, Seq(Tokenizer.apply(body)), Seq(Tokenizer.apply(orElse)))
  }

  private def dereferenceIf(testExpr: expr, tokenizedBody: Seq[Token], tokenizedOrElse: Seq[Token]): Expression = {
    val test = dereference(Tokenizer.apply(testExpr))
    val body = dereferenceScope(tokenizedBody)
    val orElse = dereferenceScope(tokenizedOrElse)

    if (test.`type`.observable || body.`type`.observable || orElse.`type`.observable) {
      functionCall(IfStatement, Seq(
        test,
        wrapScope(tokenizedBody),
        wrapScope(tokenizedOrElse)
      ))
    } else {
      val innerBody = ExpressionOperation(If, Seq(test, body, orElse), body.`type`)

      wrap(innerBody)
    }
  }

  private def wrapScope(program: Seq[Token]): Expression = {
    if (program.size == 1) {
      dereference(program.head)
    } else {
      wrap(program)
    }
  }
}

case object If extends Operator {

  override def apply(operands: List[Constant]) = {
    s"""if (${operands(0).value}) {
        |${Indent.leftPad(operands(1).value)}
        |} else {
        |${Indent.leftPad(operands(2).value)}
        |}""".stripMargin
  }
}
