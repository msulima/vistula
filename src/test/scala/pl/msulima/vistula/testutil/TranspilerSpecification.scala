package pl.msulima.vistula.testutil

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula

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
      val token = s"/pl/msulima/vistula/transpiler/$basePath/$file.vst.html"

      Vistula.toJavaScript(s""""# html:$token"""").dropRight(1) must_==
        readFile(s"/pl/msulima/vistula/transpiler/$basePath/$file.js")
    }
  }
}
