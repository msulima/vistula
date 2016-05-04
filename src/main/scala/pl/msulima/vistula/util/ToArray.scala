package pl.msulima.vistula.util

object ToArray {

  def apply(seq: Seq[_]) = {
    if (seq.isEmpty) {
      "[]"
    } else {
      "[\n" + Indent.leftPad(seq.mkString(",\n")) + "\n]"
    }
  }

  def toCompact(seq: Seq[(String, String)]) = "[" + seq.map(x => s"""[${x._1}, ${x._2}]""").mkString(", ") + "]"

  def toDict(seq: Seq[(_, _)]) = "{\n" + Indent.leftPad(seq.map({
    case (key, value) =>
      s"$key: $value"
  }).mkString(",\n")) + "\n}"
}
