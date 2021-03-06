package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, OperationDereferencer}
import pl.msulima.vistula.transpiler.scope._

trait FunctionCallDereferencer {
  this: Dereferencer with BoxDereferencer =>

  def functionCallDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Call(func, args, _, _, _))) =>
      functionCall(Tokenizer.apply(func), dereference(args.map(Tokenizer.apply)))
  }

  def functionCall(func: Token, arguments: Seq[Expression]): ExpressionOperation = {
    val function = dereferenceFunction(func, arguments)
    val funcDefinition = getDefinition(function, arguments)

    functionCall(function, funcDefinition, arguments)
  }

  private def dereferenceFunction(function: Token, arguments: Seq[_]) = {
    dereference(function) match {
      case ExpressionConstant(value, ScopeElement(true, _: ClassReference)) =>
        val definition = FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = true,
          resultIsObservable = true)
        ExpressionConstant(value, ScopeElement.const(definition))
      case func =>
        func
    }
  }

  private def getDefinition(function: Expression, arguments: Seq[_]) = {
    val functionType = function.`type`

    functionType.`type` match {
      case definition: FunctionDefinition =>
        definition.adapt(arguments)
      case _: ClassReference =>
        FunctionDefinitionHelper.adapt(arguments.size, argumentsAreObservable = functionType.observable,
          resultIsObservable = functionType.observable)
    }
  }

  def functionCall(function: Expression, funcDefinition: FunctionDefinition, arguments: Seq[Expression]): ExpressionOperation = {
    val (observables, inputs) = findSubstitutes(function, funcDefinition, arguments)
    val body = ExpressionOperation(FunctionCall(funcDefinition.constructor), inputs, funcDefinition.resultType)

    RxMapOp(observables, body)
  }

  private def findSubstitutes(function: Expression, funcDefinition: FunctionDefinition, arguments: Seq[Expression]): (Seq[Expression], Seq[Expression]) = {
    val functionSubstitutes = OperationDereferencer.extractObservables(function)
    val argumentsSubstitutes = arguments.zip(funcDefinition.arguments).map({
      case (arg, argDefinition) =>
        if (argDefinition.observable && !arg.`type`.`type`.isInstanceOf[FunctionDefinition]) {
          Seq() -> toObservable(arg)
        } else {
          OperationDereferencer.extractObservables(arg)
        }
    })

    val observables = functionSubstitutes._1 ++ argumentsSubstitutes.flatMap(_._1)
    val inputs = functionSubstitutes._2 +: argumentsSubstitutes.map(_._2)

    (observables, inputs)
  }
}

case class FunctionCall(constructor: Boolean) extends Operator {

  override def apply(operands: List[Constant]) = {
    val prefix = if (constructor) {
      "new "
    } else {
      ""
    }
    s"$prefix${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})"
  }
}
