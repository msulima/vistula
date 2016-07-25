package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer: PartialFunction[Token, Token] = {
    case operation@Operation(FunctionScope, program, _) =>
      val result = Transformer.scoped(program, scope)

      Operation(FunctionScope, result.init :+ Return(result.last))
    case Operation(FunctionCall, arguments, func) =>
      val dereferencedFunc = dereference(func)

      val (resultFunc, dereferencedInputs) = dereferencedFunc match {
        case t if FunctionScopes.functions.contains(t) =>
          substitute(t, arguments)
        case Observable(t: Constant) =>
          dereferencedFunc -> (t +: arguments.map(arg => dereference(Box(arg))))
        case _: Observable =>
          dereferencedFunc -> (dereferencedFunc +: arguments.map(arg => dereference(Box(arg))))
        case _ =>
          dereferencedFunc -> (dereferencedFunc +: arguments.map(dereference))
      }

      dereferenceOperation(FunctionCall(resultFunc, dereferencedInputs))
  }

  private def substitute(func: Token, arguments: Seq[Token]) = {
    val definition = FunctionScopes.functions(func)

    val resultFunc = if (definition.resultIsObservable) {
      Observable(func)
    } else {
      func
    }

    (resultFunc, func +: handleArguments(definition, arguments))
  }

  private def handleArguments(definition: FunctionDefinition, arguments: Seq[Token]) = {
    require(arguments.size == definition.arguments.size, s"Wrong number of arguments: ${arguments.size} expected ${definition.arguments.size}")

    arguments.zip(definition.arguments).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          dereference(Box(arg))
        } else {
          ???
        }
    })
  }
}

case class ArgumentDefinition(observable: Boolean)

case class FunctionDefinition(id: Ast.identifier, arguments: Seq[ArgumentDefinition], resultIsObservable: Boolean)

object FunctionScopes {

  private val const = ArgumentDefinition(observable = false)
  private val obs = ArgumentDefinition(observable = true)

  val VistulaIfStatement = FunctionDefinition(Ast.identifier("vistula.ifStatement"), Seq(obs, obs, obs), resultIsObservable = true)

  val functions: Map[Token, FunctionDefinition] = Map(
    Constant(VistulaIfStatement.id.name) -> VistulaIfStatement
  )
}
