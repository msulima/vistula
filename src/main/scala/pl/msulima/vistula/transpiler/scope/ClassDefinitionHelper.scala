package pl.msulima.vistula.transpiler.scope

import pl.msulima.vistula.Package
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.scope.FunctionDefinitionHelper._

object ClassDefinitionHelper {

  val VistulaRoot = Package(Seq(Ast.identifier("vistula")))

  private val VistulaSeqFactory = ClassReference("vistula.collection.SeqFactory")
  private val VistulaSeq = ClassReference("vistula.collection.Seq")
  private val VistulaDom = ClassReference("vistula.dom.Dom")
  val Vistula = VistulaRoot.packageObjectReference

  val Event = ClassReference("js.Event")

  val defaults: Map[ClassReference, ClassDefinition] = Map(
    ClassReference.Object -> ClassDefinition(Map()),
    Vistula -> ClassDefinition(Map(
      obsDef("aggregate", obs, const, const),
      obsDef("constantObservable", const),
      obsDef("ifChangedArrays", obs, const, const),
      obsDef("ifStatement", obs, obs, obs),
      obsDef("wrap", const),
      obsDef("zipAndFlatten", const),
      Ast.identifier("Seq") -> ScopeElement.const(VistulaSeqFactory),
      Ast.identifier("dom") -> ScopeElement.const(VistulaDom)
    )),
    VistulaSeqFactory -> ClassDefinition(Map(
      Ast.identifier("apply") -> ScopeElement.const(FunctionDefinition(Seq(obs),
        resultType = ScopeElement.observable(VistulaSeq), varargs = true))
    )),
    VistulaSeq -> ClassDefinition(Map(
      obsDef("filter", const)
    )),
    VistulaDom -> ClassDefinition(Map(
      obsDef("textObservable", obs),
      obsDef("textNode", const),
      obsDef("createBoundElement", const, const, const, const),
      obsDef("createElement", const, const, const)
    ))
  )

  private def obsDef(name: String, arguments: ScopeElement*) = {
    Ast.identifier(name) -> ScopeElement.const(FunctionDefinition(arguments, resultType = ScopeElement.Default))
  }
}
