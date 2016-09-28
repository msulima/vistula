package pl.msulima.vistula.util

import java.io.File
import java.nio.file.{DirectoryStream, Files, Path, Paths => JavaPaths}

import pl.msulima.vistula.Package
import pl.msulima.vistula.transpiler.scope.ClassReference

import scala.collection.JavaConversions._

object Paths {

  private val SourceDir = JavaPaths.get("src", "main", "vistula")
  private val TargetDir = JavaPaths.get("target", "vistula", "classes")

  def cleanTarget() = {
    deleteRecursively(TargetDir.toFile)
  }

  def findAllSourceFiles() = {
    findPackageSourceFiles(Package.Root)
  }

  def findPackageSourceFiles(input: Package) = {
    val path = if (input.path.isEmpty) {
      SourceDir
    } else {
      val names = input.path.map(_.name)
      SourceDir.resolve(JavaPaths.get(names.head, names.tail: _*))
    }

    getAllFiles(path, input)
  }

  private def getAllFiles(top: Path, `package`: Package): Iterable[(Path, Package)] = {
    var directoryStream: DirectoryStream[Path] = null

    try {
      directoryStream = Files.newDirectoryStream(top)
      directoryStream.flatMap(dir => {
        if (dir.toFile.isDirectory) {
          getAllFiles(dir, `package`.resolve(dir.toFile.getName))
        } else {
          Seq(dir -> `package`)
        }
      })
    } finally {
      if (directoryStream != null) {
        directoryStream.close()
      }
    }
  }

  def toTargetFile(file: Path) = {
    val subpath = file.subpath(SourceDir.getNameCount, file.getNameCount - 1)

    TargetDir.resolve(subpath).resolve(file.getFileName.toString.replaceAll("\\.vst$", ".js"))
  }

  def toTargetFile(input: Package) = {
    TargetDir.resolve(input.join + ".js")
  }

  private def deleteRecursively(f: File) {
    if (f.isDirectory) {
      f.listFiles().foreach(deleteRecursively)
    }
    f.delete()
  }

  def findSourceFile(id: ClassReference) = {
    val prefix = id.`package`.path.map(_.name) :+ (id.name.name + ".vst")

    prefix.foldLeft(SourceDir)((acc, file) => {
      acc.resolve(file)
    })
  }
}
