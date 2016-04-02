package pl.msulima.vistula.scanner

object Flatter {

  def apply(variable: Variable): Seq[FlatVariable] = {
    variable match {
      case obs: Observable =>
        val flatDependencies = obs.dependsOn.flatMap(apply)

        flatDependencies :+ FlatVariable(Some(obs.name), obs.expression, obs.dependsOn.flatMap(flatten))
      case _: Constant | _: NamedObservable =>
        Seq()
    }
  }

  private def flatten(variable: Variable): Option[NamedObservable] = {
    variable match {
      case x: Observable =>
        Some(NamedObservable(x.name))
      case x: NamedObservable =>
        Some(x)
      case x: Constant =>
        None
    }
  }
}
