import java.io.File
import java.nio.file.Files

import akka.actor.ActorSystem
import org.joda.time.{DateTime, Hours, Interval, LocalTime, Seconds}
import scopt.OParser

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import implicits.Implicits._
import shapeless.{HList, LabelledGeneric}
import shapeless.ops.maps.FromMap
import tool.ConvertHelper
import utils.Utils
import scala.language.postfixOps

case class Config(
                   dbDir: File = new File("C:\\ip4m_database"),
                   outDir: File = new File("C:\\temp"),
                 )

/**
 * Created by yz on 2019/4/8
 */
object Main {

  def main(args: Array[String]): Unit = {

    val builder = OParser.builder[Config]
    val parser1 = {
      import builder._
      //o d
      OParser.sequence(
        programName("scopt"),
        head("scopt", "4.x"),
        opt[File]('d', "dbDir").valueName("<file>").action { (x, c) =>
          c.copy(dbDir = x)
        }.text("database dir!").required(),
        opt[File]('o', "outDir").valueName("<file>").action { (x, c) =>
          c.copy(outDir = x)
        }.text("output dir!").required(),
        help("help").text("prints this usage text")
      )
    }
    OParser.parse(parser1, args, Config()) match {
      case Some(config) => exec(config)
      case _ =>
    }

    def exec(config: Config) = {
      call(config)
      val execTime = new DateTime()
      val delay = {
        val time = DateTime.now().withHourOfDay(2).plusDays(1)
        Hours.hoursBetween(execTime, time).getHours
      }
      val system = ActorSystem("ip4mMessageBackup")
      system.scheduler.schedule(delay hours, 5 days)(() => {
        call(config)
      })

    }

    def call(config: Config) = {
      val parent = config.outDir
      val messageFile = new File(parent, "message.csv")
      val dbMissionIds = if (messageFile.exists()) {
        messageFile.csvLines.lineMap.map(_ ("missionId"))
      } else List[String]()
      case class MyMessage(missionId: String, isTargetTest: String, client: String, affiliation: String,
                           email: String, projectCode: String, salesRep: String,
                           testOrderName: String, testOrderId: String,
                           sampleType: String)
      val userDir = new File(config.dbDir, "user")
      val myMessages = userDir.listFiles().flatMap { userIdDir =>
        val missionDir = new File(userIdDir, "mission")
        missionDir.listFiles().map { missionIdDir =>
          val missionId = missionIdDir.getName
          val configMap = if (dbMissionIds.contains(missionId)) {
            Map[String, Any]()
          } else {
            val workspaceDir = new File(missionIdDir, "workspace")
            val configFile = new File(workspaceDir, "config.txt")
            if (configFile.exists()) {
              val fieldNames = Utils.getFieldNamesByTs(List[MyMessage]())
              val map = configFile.csvLines.map { columns =>
                columns(0).replaceAll("^reportHome\\.", "") -> columns(1)
              }.toMap
              fieldNames.map { fieldName =>
                if (map.contains(fieldName)) (fieldName, map(fieldName)) else (fieldName, "")
              }.toMap.updated("missionId", missionId)
            } else Map[String, Any]()
          }
          import FromMap._
          Utils.to[MyMessage].from(configMap)
        }
      }
      val trueMyMessages = myMessages.filter(_.isDefined).map(_.get).toList
      if (trueMyMessages.nonEmpty) {
        val now = new DateTime().toString("yyyy_MM_dd_HH_mm_ss")
        if (messageFile.exists()) messageFile.copyTo(new File(parent, s"message_${now}.txt"))
        val lines = Utils.getLinesByTs(trueMyMessages)
        val newLines = if (messageFile.exists()) {
          messageFile.csvLines ::: lines.drop(1)
        } else lines
        newLines.toFile(messageFile)
      }
    }


  }

}
