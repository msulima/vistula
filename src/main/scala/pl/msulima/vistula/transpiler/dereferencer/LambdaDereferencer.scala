package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.function.FunctionDereferencer
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.scope.FunctionReference

trait LambdaDereferencer {
  this: Dereferencer with FunctionDereferencer =>

  def lambdaDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Lambda(args, body))) =>
      val transpiledBody = Tokenizer.apply(body)
      val arguments = FunctionDef.mapArguments(args)

      dereferenceFunction(FunctionDef(FunctionReference.Anonymous, Seq(transpiledBody), arguments))
  }
}
