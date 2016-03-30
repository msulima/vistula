package pl.msulima.vistula

case class Nesting(levels: Seq[Int]) {

  override def toString = s"$$${levels.mkString("_", "_", "")}"

  def extend = Nesting(levels :+ 0)
}
