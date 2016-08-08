package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  private val VistulaSeq = ClassReference("vistula.collection.Seq")
  private val VistulaDom = ClassReference("vistula.dom.Dom")
  val Vistula = ClassReference("vistula.Predef")

  val defaults: Map[ClassReference, ClassDefinition] = Map(
    ClassReference.Object -> ClassDefinition(Map()),
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, obs, const),
      obsDef("constantObservable", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("ifStatement", obs, obs, obs),
      obsDef("wrap", const),
      obsDef("zipAndFlatten", const),
      Ast.identifier("Seq") -> ScopeElement(observable = false, `type` = VistulaSeq),
      Ast.identifier("dom") -> ScopeElement(observable = false, `type` = VistulaDom)
    )),
    VistulaSeq -> ClassDefinition(Map(
      Ast.identifier("apply") -> ScopeElement(observable = false, FunctionDefinition(Seq(obs), resultIsObservable = true, varargs = true))
    )),
    VistulaDom -> ClassDefinition(Map(
      obsDef("textObservable", obs),
      obsDef("textNode", const),
      obsDef("createBoundElement", const, const, const, const),
      obsDef("createElement", const, const, const)
    ))
  )

  private def constDef(name: String, arguments: ScopeElement*) = {
    Ast.identifier(name) -> ScopeElement(observable = false, FunctionDefinition(arguments, resultIsObservable = false))
  }

  private def obsDef(name: String, arguments: ScopeElement*) = {
    Ast.identifier(name) -> ScopeElement(observable = false, FunctionDefinition(arguments, resultIsObservable = true))
  }
}
