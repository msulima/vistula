package pl.msulima.vistula.util

object ToArray {

  def apply(seq: Seq[_]) = {
    if (seq.isEmpty) {
      "[]"
    } else {
      "[\n" + Indent.leftPad(seq.mkString(",\n")) + "\n]"
    }
  }

  def toDict(seq: Seq[(_, _)]) = {
    if (seq.isEmpty) {
      "{}"
    } else {
      "{\n" + Indent.leftPad(seq.map({
        case (key, value) =>
          s"$key: $value"
      }).mkString(",\n")) + "\n}"
    }
  }
}
