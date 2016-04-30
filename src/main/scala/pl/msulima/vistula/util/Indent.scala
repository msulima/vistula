package pl.msulima.vistula.util

object Indent {
  def ind(indent: Int) = "   +" * indent

  def leftPad(xs: Seq[String]) = xs.flatMap(_.split("\n")).map("    " + _).mkString("\n")

  def leftPad(xs: String) = xs.split("\n").map("    " + _).mkString("\n")
}
