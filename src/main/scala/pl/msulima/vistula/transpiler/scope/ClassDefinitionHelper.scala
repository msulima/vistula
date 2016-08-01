package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  val Vistula = Ast.identifier("Vistula")

  val defaults = Seq(
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, obs, const),
      obsDef("zipAndFlatten", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("wrap", const),
      obsDef("ifStatement", obs, obs, obs)
    ))
  )
}
