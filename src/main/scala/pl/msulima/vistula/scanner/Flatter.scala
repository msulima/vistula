package pl.msulima.vistula.scanner

object Flatter {

  def apply(program: Seq[Variable]): Seq[Variable] = {
    program.flatMap(apply)
  }

  def apply(variable: Variable): Seq[Variable] = {
    variable match {
      case obs: Observable =>
        val flatDependencies = obs.dependsOn.flatMap(apply)
        val flatObs = obs.copy(dependsOn = obs.dependsOn.map(flatten))
        flatDependencies :+ flatObs
      case _: Constant | _: NamedObservable =>
        Seq()
    }
  }

  private def flatten(variable: Variable): Variable = {
    variable match {
      case x: Observable =>
        NamedObservable(x.name)
      case x@(_: Constant | _: NamedObservable) =>
        x
    }
  }
}
