package test

import java.io.File

import shapeless.labelled._
import shapeless._
import implicits.Implicits._
import tool.{CaseClassFromMap, ConvertHelper, FromMap}
import utils.Utils

/**
 * Created by yz on 6/3/2020
 */
object Test {

  case class MyMessage(isTargetTest: String, client: String, affiliation: String,
                       email: String, projectCode: String, salesRep: String,
                       testOrderName: String, testOrderId: String,
                       sampleType: String)

  def main(args: Array[String]): Unit = {

    def to[A]: ConvertHelper[A] = {
      import tool.FromMap._
      new ConvertHelper[A]
    }

    val configFile = new File("C:\\ip4m_database\\user\\3\\mission\\1987\\workspace\\config.txt")
    val configMap = if (configFile.exists()) {
      val fieldNames = Utils.getFieldNamesByTs(List[MyMessage]())
      val map = configFile.csvLines.map { columns =>
        columns(0).replaceAll("^reportHome\\.", "") -> columns(1)
      }.toMap
      fieldNames.map { fieldName =>
        if (map.contains(fieldName)) (fieldName, map(fieldName)) else (fieldName, "")
      }.toMap
    } else Map[String, Any]()
    println(configMap)
    val rs = to[MyMessage].from(configMap)
    println(rs)

    trait CaseClassFromMap[P <: Product] {
      def apply(m: Map[String, Any]): Option[P]
    }



    def usesGenerics[P <: Product](map: Map[String, Any])(implicit fromMap: CaseClassFromMap[P]): P = {
      fromMap(map).get
    }


    import CaseClassFromMap._
    import FromMap._

    case class MyData(name: String, age: Option[Int])
    val rs1 = CaseClassFromMap[MyData](Map("name" -> "yz", "age" -> None))
    println(rs1)


  }


}
