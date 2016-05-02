package pl.msulima.vistula.util

object ToArray {

  def apply[T](seq: Seq[T]) = seq.mkString("[", ", ", "]")
}
