package implicits

import java.io.File

import implicits.Implicits._
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

/**
 * Created by Administrator on 2019/9/12
 */
trait MyFileTool {

  implicit class MyFile(file: File) {

    def unixPath = {
      val path = file.getAbsolutePath
      path.unixPath
    }

    def lines: List[String] = lines()

    def txtLines = lines.map(_.split("\t").toList)

    def lines(encoding: String = "UTF-8") = FileUtils.readLines(file, encoding).asScala.toList

    def str = FileUtils.readFileToString(file, "UTF-8")

    def allFiles: List[File] = {

      def loop(acc: List[File], files: List[File]): List[File] = {
        files match {
          case Nil => acc
          case x :: xs => x.isDirectory match {
            case false => loop(x :: acc, xs)
            case true => loop(acc, xs ::: x.listFiles().toList)
          }
        }
      }

      loop(List(), List(file))
    }

    def createDirectoryWhenNoExist = {
      if (!file.exists && !file.isDirectory) FileUtils.forceMkdir(file)
      file
    }

    def namePrefix: String = {
      val fileName = file.getName
      fileName.fileNamePrefix
    }

    def deleteQuietly = {
      FileUtils.deleteQuietly(file)
    }

    def copyTo(destFile: File) = {
      FileUtils.copyFile(file, destFile)
    }

  }


}
