package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  private val VistulaSeqFactory = ClassReference("vistula.collection.SeqFactory")
  private val VistulaSeq = ClassReference("vistula.collection.Seq")
  private val VistulaDom = ClassReference("vistula.dom.Dom")
  val Vistula = ClassReference("vistula.Predef")
  val Event = ClassReference("vistula.dom.Event")

  val defaults: Map[ClassReference, ClassDefinition] = Map(
    ClassReference.Object -> ClassDefinition(Map()),
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, obs, const),
      obsDef("constantObservable", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("ifStatement", obs, obs, obs),
      obsDef("wrap", const),
      obsDef("zipAndFlatten", const),
      Ast.identifier("Seq") -> ScopeElement(observable = false, `type` = VistulaSeqFactory),
      Ast.identifier("dom") -> ScopeElement(observable = false, `type` = VistulaDom)
    )),
    VistulaSeqFactory -> ClassDefinition(Map(
      Ast.identifier("apply") -> ScopeElement(observable = false, FunctionDefinition(Seq(obs),
        resultType = ScopeElement(observable = true, `type` = VistulaSeq), varargs = true))
    )),
    VistulaSeq -> ClassDefinition(Map(
      obsDef("filter", const)
    )),
    VistulaDom -> ClassDefinition(Map(
      obsDef("textObservable", obs),
      obsDef("textNode", const),
      obsDef("createBoundElement", const, const, const, const),
      obsDef("createElement", const, const, const)
    )),
    Event -> ClassDefinition(Map(
      constDef("preventDefault")
    ))
  )

  private def constDef(name: String, arguments: ScopeElement*) = {
    Ast.identifier(name) -> ScopeElement(observable = false,
      FunctionDefinition(arguments, resultType = ScopeElement(observable = false, ClassReference.Object)))
  }

  private def obsDef(name: String, arguments: ScopeElement*) = {
    Ast.identifier(name) -> ScopeElement(observable = false,
      FunctionDefinition(arguments, resultType = ScopeElement.Default))
  }
}
