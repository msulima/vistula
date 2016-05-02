package pl.msulima.vistula.util

object ToArray {

  def apply[T](seq: Seq[T]) = "[\n" + Indent.leftPad(seq.mkString(",\n")) + "\n]"
}
