package pl.msulima.vistula.util

import java.io.File
import java.nio.file.{DirectoryStream, Files, Path}

import scala.collection.JavaConversions._

object Paths {

  def getAllFiles(top: Path): Iterable[(Path, Seq[String])] = {
    getAllFiles(top, Seq())
  }

  private def getAllFiles(top: Path, name: Seq[String]): Iterable[(Path, Seq[String])] = {
    var directoryStream: DirectoryStream[Path] = null

    try {
      directoryStream = Files.newDirectoryStream(top)
      directoryStream.flatMap(dir => {
        if (dir.toFile.isDirectory) {
          getAllFiles(dir, name :+ dir.toFile.getName)
        } else {
          Seq(dir -> name)
        }
      })
    } finally {
      directoryStream.close()
    }
  }

  def deleteRecursively(f: File) {
    if (f.isDirectory) {
      f.listFiles().foreach(deleteRecursively)
    }
    f.delete()
  }
}
