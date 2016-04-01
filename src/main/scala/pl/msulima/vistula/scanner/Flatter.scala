package pl.msulima.vistula.scanner

object Flatter {

  def apply(program: Seq[Variable]): Seq[Variable] = {
    program.flatMap(apply)
  }

  def apply(variable: Variable): Seq[Variable] = {
    variable match {
      case x: Constant =>
        Seq()
      case x: NamedObservable =>
        Seq()
      case x: Observable =>
        x.dependsOn.flatMap(apply) :+ x.copy(dependsOn = x.dependsOn.map(flatten))
    }
  }

  private def flatten(variable: Variable): Variable = {
    variable match {
      case x: Constant =>
        x
      case x: NamedObservable =>
        x
      case x: Observable =>
        NamedObservable(x.name)
    }
  }
}
