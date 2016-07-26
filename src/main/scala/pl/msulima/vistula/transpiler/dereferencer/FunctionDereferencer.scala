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
      val dereferencedFunc: Token = dereference(func)

      val (funcDefinition, function) = dereferencedFunc match {
        case t if FunctionSymbols.functions.contains(t) =>
          FunctionSymbols.functions(t) -> t
        case Observable(t: Constant) =>
          val definition = FunctionDefinition.adapt(arguments.size, argumentsAreObservable = true, resultIsObservable = true)
          definition -> t
        case _: Observable =>
          val definition = FunctionDefinition.adapt(arguments.size, argumentsAreObservable = true, resultIsObservable = true)
          definition -> dereferencedFunc
        case _ =>
          val definition = FunctionDefinition.adapt(arguments.size, argumentsAreObservable = false, resultIsObservable = false)
          definition -> dereferencedFunc
      }

      val (resultFunc, dereferencedInputs) = substitute(funcDefinition, function, arguments)
      dereferenceOperation(FunctionCall(resultFunc, dereferencedInputs))
  }

  private def substitute(definition: FunctionDefinition, func: Token, arguments: Seq[Token]): (Token, Seq[Token]) = {
    val resultFunc = if (definition.resultIsObservable) {
      Observable(func)
    } else {
      func
    }

    (resultFunc, func +: handleArguments(definition, arguments))
  }

  private def handleArguments(definition: FunctionDefinition, arguments: Seq[Token]) = {
    val args = if (definition.varargs) {
      FunctionDefinition.adaptArguments(arguments.size, definition.arguments.head.observable)
    } else {
      require(arguments.size == definition.arguments.size,
        s"Wrong number of arguments: given ${arguments.size} expected ${definition.arguments.size}")
      definition.arguments
    }

    arguments.zip(args).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable) {
          dereference(Box(arg))
        } else {
          dereference(arg)
        }
    })
  }
}

case class ArgumentDefinition(observable: Boolean)

case class FunctionDefinition(id: Ast.identifier, arguments: Seq[ArgumentDefinition], resultIsObservable: Boolean, varargs: Boolean = false)

object FunctionDefinition {

  val const = ArgumentDefinition(observable = false)
  val obs = ArgumentDefinition(observable = true)

  def adapt(argumentsCount: Int, argumentsAreObservable: Boolean, resultIsObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    FunctionDefinition(Ast.identifier(""), (1 to argumentsCount).map(_ => argumentDefinition), resultIsObservable = resultIsObservable)
  }

  def adaptArguments(argumentsCount: Int, argumentsAreObservable: Boolean) = {
    val argumentDefinition = if (argumentsAreObservable) {
      obs
    } else {
      const
    }
    (1 to argumentsCount).map(_ => argumentDefinition)
  }

  def constDef(name: String, arguments: ArgumentDefinition*) = {
    FunctionDefinition(Ast.identifier(name), arguments, resultIsObservable = false)
  }

  def obsDef(name: String, arguments: ArgumentDefinition*) = {
    FunctionDefinition(Ast.identifier(name), arguments, resultIsObservable = true)
  }
}

object FunctionSymbols {

  import FunctionDefinition._

  private val definitions = Seq(
    obsDef("vistula.ifStatement", obs, obs, obs),
    obsDef("vistula.dom.textObservable", obs),
    obsDef("vistula.dom.textNode", const),
    obsDef("vistula.zipAndFlatten", const),
    obsDef("vistula.aggregate", obs, obs, const),
    constDef("vistula.ifChangedArrays", obs, const, const),
    constDef("vistula.dom.createBoundElement", const, const, const, const),
    constDef("vistula.dom.createElement", const, const, const),
    FunctionDefinition(Ast.identifier("vistula.Seq.apply"), Seq(obs), resultIsObservable = true, varargs = true)
  )

  val functions: Map[Token, FunctionDefinition] = definitions.map(d => Constant(d.id.name) -> d).toMap
}
