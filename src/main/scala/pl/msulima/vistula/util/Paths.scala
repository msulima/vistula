package pl.msulima.vistula.util

import java.io.File
import java.nio.file.{DirectoryStream, Files, Path}

import pl.msulima.vistula.parser.Ast

import scala.collection.JavaConversions._

object Paths {

  private val SourceDir = java.nio.file.Paths.get("src", "main", "vistula")
  private val TargetDir = java.nio.file.Paths.get("target", "vistula", "classes")

  def cleanTarget() = {
    deleteRecursively(TargetDir.toFile)
  }

  def findAllSourceFiles() = {
    getAllFiles(SourceDir)
  }

  def toTargetFile(file: Path) = {
    val subpath = file.subpath(SourceDir.getNameCount, file.getNameCount - 1)

    TargetDir.resolve(subpath).resolve(file.getFileName.toString.replaceAll("\\.vst$", ".js"))
  }

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

  private def deleteRecursively(f: File) {
    if (f.isDirectory) {
      f.listFiles().foreach(deleteRecursively)
    }
    f.delete()
  }

  def findSourceFile(id: Ast.identifier) = {
    val imported = id.name.split("\\.")
    val prefix = imported.init :+ (imported.last + ".vst")

    prefix.foldLeft(SourceDir)((acc, file) => {
      acc.resolve(file)
    })
  }
}
