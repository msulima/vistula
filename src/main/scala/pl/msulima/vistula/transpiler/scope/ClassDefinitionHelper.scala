package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  val Vistula = Ast.identifier("vistula.Predef")
  private val VistulaSeq = Ast.identifier("vistula.collection.Seq")
  private val VistulaDom = Ast.identifier("vistula.dom.Dom")

  val defaults = Seq(
    ClassDefinition.Object -> ClassDefinition(Map()),
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, obs, const),
      obsDef("zipAndFlatten", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("wrap", const),
      obsDef("ifStatement", obs, obs, obs),
      Ast.identifier("Seq") -> Identifier(observable = false, `type` = VistulaSeq),
      Ast.identifier("dom") -> Identifier(observable = false, `type` = VistulaDom)
    )),
    VistulaSeq -> ClassDefinition(Map(
      Ast.identifier("apply") -> FunctionDefinition(Seq(obs), resultIsObservable = true, varargs = true)
    )),
    VistulaDom -> ClassDefinition(Map(
      obsDef("textObservable", obs),
      obsDef("textNode", const),
      obsDef("createBoundElement", const, const, const, const),
      obsDef("createElement", const, const, const)
    ))
  )
}
