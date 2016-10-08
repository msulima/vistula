package pl.msulima.vistula.transpiler.dereferencer.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.{FunctionCallDereferencer, LambdaDereferencer}
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{ScopeElement, Variable}

trait LoopDereferencer {
  this: Dereferencer with FunctionCallDereferencer with LambdaDereferencer =>

  private val MapFunction = Ast.identifier("map")

  def loopDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.For(Ast.expr.Name(variable, Ast.expr_context.Load), iterable, body)) =>
      val mapFunction = Reference(Tokenizer.apply(iterable), MapFunction)
      val program = body.map(Tokenizer.applyStmt)
      val boxLast = program.init :+ Box(program.last)

      val argument = dereferenceLambda(Seq(Variable(variable, ScopeElement.Default)), boxLast)

      functionCall(mapFunction, Seq(argument))
  }
}
