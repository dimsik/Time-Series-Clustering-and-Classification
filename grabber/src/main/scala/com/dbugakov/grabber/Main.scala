package com.dbugakov.grabber

import java.io._
import java.util.Properties

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import spray.json.JsonParser

import scala.collection.mutable.ArrayBuffer

object Main {


  def main(args: Array[String]) {

    val (fileName, apiKey) = {
      val prop = new Properties()
      prop.load(new FileInputStream("config.properties"))
      (
        prop.getProperty("file.name"),
        prop.getProperty("openweathermap.apikey")
        )
    }

    (new Worker(fileName, apiKey)).run()


  }

}

class Worker(fileName: String, apiKey: String) extends Runnable {
  def run(): Unit = {
    while (true) {
      val cities: List[String] = List("Moscow,RU", "London", "Tokyo", "Kiev", "Amsterdam,NL",
        "Ankara", "Berlin", "Brussels", "Oslo", "Paris,FR")
      val dataBuffer = ArrayBuffer.empty[String]

      for (city <- cities) {
        Thread.sleep(10000)

        val result = {
          val httpClient = new DefaultHttpClient()
          val httpResponse = httpClient.execute(new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=" + city + apiKey));
          val entity = httpResponse.getEntity()

          var content = ""
          if (entity != null) {
            val inputStream = entity.getContent()
            content = io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
          }
          httpClient.getConnectionManager().shutdown()
          content
        }

        val data = {
          val fields = JsonParser(result).asJsObject.fields
          val mainFields = fields.get("main").toList(0).asJsObject
          val coord = fields.get("coord").toList(0).asJsObject
          val preData = ArrayBuffer.empty[String]
          preData += coord.getFields("lat")(0).toString()
          preData += coord.getFields("lon")(0).toString()
          preData += fields.get("dt").toList(0).toString()
          preData += mainFields.getFields("pressure")(0).toString()
          preData += mainFields.getFields("humidity")(0).toString()
          preData += mainFields.getFields("temp")(0).toString()
          preData.mkString(";")
        }

        dataBuffer += data

      }

      lazy val print = {
        val printWriter = new PrintWriter(new FileOutputStream(new File(fileName), true))
        printWriter
      }

      if (dataBuffer.length == 100) {
        for (data <- dataBuffer) {
          print.write(data + '\n')
        }
      }
      dataBuffer.clear()
      print.close()
    }
  }
}
