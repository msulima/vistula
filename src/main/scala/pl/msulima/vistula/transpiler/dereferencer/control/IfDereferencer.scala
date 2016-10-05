package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.expr
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.function.FunctionDereferencer
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, FunctionCallDereferencer}
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, FunctionReference, Scope, ScopeElement}
import pl.msulima.vistula.util.Indent

trait IfDereferencer {
  this: Dereferencer with FunctionCallDereferencer with FunctionDereferencer =>

  private val IfStatement = Reference(Reference(Scope.VistulaHelper), Ast.identifier("ifStatement"))
  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))

  def ifDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.If(testExpr, body, orElse)) =>
      dereferenceIf(testExpr, body.map(Tokenizer.applyStmt), orElse.map(Tokenizer.applyStmt))
    case Direct(Ast.stmt.Expr(Ast.expr.IfExp(testExpr, body, orElse))) =>
      dereferenceIf(testExpr, Seq(Tokenizer.apply(body)), Seq(Tokenizer.apply(orElse)))
  }

  private def dereferenceIf(testExpr: expr, tokenizedBody: Seq[Token], tokenizedOrElse: Seq[Token]): Expression = {
    val test = dereference(Tokenizer.apply(testExpr))
    val body = wrapScope(tokenizedBody)
    val orElse = wrapScope(tokenizedOrElse)

    if (test.`type`.observable || body.`type`.observable || orElse.`type`.observable) {
      functionCall(IfStatement, Seq(
        test,
        body,
        orElse
      ))
    } else {
      val branches = Seq(
        test,
        dereferenceScope(tokenizedBody.map(dereference)),
        dereferenceScope(tokenizedOrElse.map(dereference))
      )

      val innerBody = ExpressionOperation(If, branches, body.`type`)

      val func = FunctionDef(FunctionReference.Anonymous, Seq(), Seq())
      val funcDefinition = FunctionDefinition(Seq(), innerBody.`type`)
      val innerFunction = ExpressionOperation(func, Seq(innerBody), ScopeElement.const(funcDefinition))

      functionCall(Wrap, Seq(innerFunction))
    }
  }

  private def wrapScope(program: Seq[Token]): Expression = {
    if (program.size == 1) {
      dereference(program.head)
    } else {
      functionCall(Wrap, Seq(anonymousFunction(Seq(), program)))
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
