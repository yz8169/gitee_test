package implicits

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils

import scala.util.Try

/**
 * Created by Administrator on 2019/9/12
 */
trait MyStringTool {

  implicit class MyString(v: String) {

    def isInt = {
      Try(v.toInt).toOption match {
        case Some(value) => true
        case None => false
      }
    }

    def isDouble = {
      Try(v.toDouble).toOption match {
        case Some(value) => true
        case None => false
      }
    }

    def replaceLf={
      v.replaceAll("\n"," ").replaceAll("\r"," ")
    }

    def unixPath = {
      v.replace("\\", "/").replaceAll("D:", "/mnt/d").
        replaceAll("E:", "/mnt/e").replaceAll("C:", "/mnt/c").
        replaceAll("G:", "/mnt/g")
    }

    def startWithsIgnoreCase(prefix: String) = {
      v.toLowerCase.startsWith(prefix.toLowerCase)
    }

    def toFile(file: File, encoding: String = "UTF-8", append: Boolean = false): Unit = {
      FileUtils.writeStringToFile(file, v, encoding, append)
    }

    def trimQuote = {
      v.replaceAll("^\"", "").replaceAll("\"$", "")
    }

    def mySplit(sep: String = "\t") = {
      v.split(sep).toList
    }

    def isBlank = StringUtils.isBlank(v)

    def isValidRVar = {
      (!v.matches("^\\.\\d+.*$")) && !(v.matches("^[\\d_]+.*$")) && (v.matches("^[\\w\\.]+$"))
    }



    def fileNamePrefix: String = {
      val index = v.lastIndexOf(".")
      v.substring(0, index)
    }

  }


}
