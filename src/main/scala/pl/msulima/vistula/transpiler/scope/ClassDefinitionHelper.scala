package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  private val VistulaSeq = ClassDefinition(Ast.identifier("vistula.collection.Seq"), Map(
    Ast.identifier("apply") -> ScopeElement(observable = false, FunctionDefinition(Seq(obs), resultIsObservable = true, varargs = true))
  ))
  private val VistulaDom = ClassDefinition(Ast.identifier("vistula.dom.Dom"), Map(
    obsDef("textObservable", obs),
    obsDef("textNode", const),
    obsDef("createBoundElement", const, const, const, const),
    obsDef("createElement", const, const, const)
  ))

  val Vistula = ClassDefinition(Ast.identifier("vistula.Predef"), Map(
    obsDef("aggregate", obs, obs, const),
    obsDef("constantObservable", const),
    obsDef("ifChangedArrays", obs, const, const),
    obsDef("ifStatement", obs, obs, obs),
    obsDef("wrap", const),
    obsDef("zipAndFlatten", const),
    Ast.identifier("Seq") -> ScopeElement(observable = false, `type` = VistulaSeq),
    Ast.identifier("dom") -> ScopeElement(observable = false, `type` = VistulaDom)
  ))

  val defaults = Seq(
    ClassDefinition.Object.name -> ClassDefinition.Object,
    Vistula.name -> Vistula,
    VistulaSeq.name -> VistulaSeq,
    VistulaDom.name -> VistulaDom
  )
}
