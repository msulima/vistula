package pl.msulima.vistula.transpiler.expression

import java.nio.file.Paths

import pl.msulima.vistula.Vistula
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.ClassReference

object Other {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case classDef@Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      IntroduceClass(classDef)
    case classDef@Ast.stmt.Import(Ast.alias(identifier, None) +: _) =>
      val imported = identifier.name.split("\\.")
      val prefix = imported.init
      val path = (Seq("main", "vistula") ++ prefix) :+ (imported.last + ".vst")

      val scope = Vistula.loadFile(Paths.get("src", path: _*)).declarations

      Import(scope.copy(classes = scope.classes.map({
        case (id, definition) =>
          ClassReference((prefix :+ id.name).map(Ast.identifier)) -> definition
      })))
  }
}
