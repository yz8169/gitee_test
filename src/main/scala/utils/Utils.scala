package utils

import java.io.File

import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import shapeless.ops.hlist.ToList
import shapeless.ops.record.{Keys, Values}
import shapeless.{HList, LabelledGeneric}

import scala.collection.JavaConverters._
import shapeless.record._
import tool.ConvertHelper

/**
 * Created by yz on 2019/4/8
 */
object Utils {

  val isWindows = false

  def callScript(tmpDir: File, shBuffer: Seq[String]) = {
    val execCommand = new ExecCommand
    val runFile = if (Utils.isWindows) {
      new File(tmpDir, "run.bat")
    } else {
      new File(tmpDir, "run.sh")
    }
    FileUtils.writeLines(runFile, shBuffer.asJava)
    val shCommand = runFile.getAbsolutePath
    if (Utils.isWindows) {
      execCommand.exec(shCommand, tmpDir)
    } else {
      val useCommand = "chmod +x " + runFile.getAbsolutePath
      val dos2Unix = "dos2unix " + runFile.getAbsolutePath
      execCommand.exec(dos2Unix, useCommand, shCommand, tmpDir)
    }
    execCommand
  }

  def deleteDirectory(direcotry: File) = {
    try {
      FileUtils.deleteDirectory(direcotry)
    } catch {
      case _ =>
    }
  }

  def dataTime2String(dateTime: DateTime) = dateTime.toString("yyyy_MM_dd")

  def getLinesByTs[T, R <: HList, K <: HList, V <: HList](ys: List[T])(
    implicit gen: LabelledGeneric.Aux[T, R], keys: Keys.Aux[R, K],
    values: Values.Aux[R, V],
    ktl: ToList[K, Symbol],
    vtl: ToList[V, Any]
  ) = {
    val fieldNames = keys().toList.map(_.name)
    val lines = ys.map { y =>
      gen.to(y).values.toList.map(_.toString)
    }
    fieldNames :: lines
  }

  def getFieldNamesByTs[T, R <: HList, K <: HList, V <: HList](ys: List[T])(
    implicit gen: LabelledGeneric.Aux[T, R], keys: Keys.Aux[R, K], ktl: ToList[K, Symbol]) = {
    keys().toList.map(_.name)
  }

  def to[A]: ConvertHelper[A] = {
    import tool.FromMap._
    new ConvertHelper[A]
  }


}
