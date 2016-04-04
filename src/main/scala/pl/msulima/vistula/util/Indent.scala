package pl.msulima.vistula.util

object Indent {
  def ind(indent: Int) = "   +" * indent

  def leftPad(xs: String) = xs.split("\n").map("  " + _).mkString("\n")
}
