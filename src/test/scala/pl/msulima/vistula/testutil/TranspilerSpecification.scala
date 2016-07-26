package pl.msulima.vistula.testutil

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula
import pl.msulima.vistula.template.transpiler.Template
import pl.msulima.vistula.transpiler.Transpiler

trait TranspilerSpecification {
  this: Specification =>

  def transpileAndCompare(basePath: String)(file: String) = {
    file in {
      Vistula.toJavaScript(readFile(s"/pl/msulima/vistula/transpiler/$basePath/$file.vst")) must_==
        readFile(s"/pl/msulima/vistula/transpiler/$basePath/$file.js")
    }
  }

  def transpileAndCompareHtml(basePath: String)(file: String) = {
    file in {
      Transpiler(Template(readFile(s"/pl/msulima/vistula/transpiler/$basePath/$file.vst.html"))) must_==
        readFile(s"/pl/msulima/vistula/transpiler/$basePath/$file.js")
    }
  }
}
