package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.ToArray

sealed trait Mapper {

  def mapMethod: String
}

case object Static extends Mapper {

  override def mapMethod = "rxMap"
}

case class CodeTemplate(template: String, mapper: Mapper, dependencies: Seq[Ast.expr] = Seq()) {

  def resolve(upstream: Seq[Result]) = {
    val mutableDependencies = upstream.filter(_.mutable)

    if (mutableDependencies.isEmpty) {
      static(upstream)
    } else if (mutableDependencies.size == 1) {
      oneMutableDependency(upstream, mutableDependencies)
    } else {
      manyMutableDependencies(upstream, mutableDependencies)
    }
  }

  private def static(upstream: Seq[Result]) = {
    template.format(upstream.map(_.code): _*)
  }

  private def oneMutableDependency(upstream: Seq[Result], mutableDependencies: Seq[Result]): String = {
    val operands = upstream.map({
      case result if result.mutable =>
        "$arg"
      case result =>
        result.code
    })

    val code = template.format(operands: _*)
    s"""${mutableDependencies.head.code}.${mapper.mapMethod}($$arg => ($code))"""
  }

  private def manyMutableDependencies(upstream: Seq[Result], mutableDependencies: Seq[Result]): String = {
    var mutableOperandsIndex = 0
    val operands = upstream.map({
      case result if result.mutable =>
        val op = s"$$args[$mutableOperandsIndex]"
        mutableOperandsIndex = mutableOperandsIndex + 1
        op
      case result =>
        result.code
    })
    val arguments = ToArray(mutableDependencies.map(_.code))
    val code = template.format(operands: _*)
    s"""vistula.zip($arguments).${mapper.mapMethod}($$args => ($code))"""
  }
}
