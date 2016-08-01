package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  val Vistula = Ast.identifier("Vistula")
  val VistulaSeq = Ast.identifier("Vistula.Seq")

  val defaults = Seq(
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, obs, const),
      obsDef("zipAndFlatten", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("wrap", const),
      obsDef("ifStatement", obs, obs, obs),
      Ast.identifier("Seq") -> Identifier(observable = false, `type` = VistulaSeq)
    )),
    VistulaSeq -> ClassDefinition(Map(
      Ast.identifier("apply") -> FunctionDefinition(Seq(obs), resultIsObservable = true, varargs = true)
    ))
  )
}
