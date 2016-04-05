import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import spray.json.JsonParser

import scala.collection.mutable.ArrayBuffer
import java.io._

object Main {

  def main(args: Array[String]) {
    while(true){
      val httpClient = new DefaultHttpClient();
      val httpResponse = httpClient.execute(new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=Moskow&APPID=8816ee47eeaf32a2cc592b0ca57ac742"));
      val entity = httpResponse.getEntity()
      var content = ""
      if (entity != null) {
        val inputStream = entity.getContent()
        content = io.Source.fromInputStream(inputStream).getLines.mkString
        inputStream.close
      }
      httpClient.getConnectionManager().shutdown()
//      println(content)
      val str = JsonParser(content)
      val fields = str.asJsObject.fields

      val mainFields = fields.get("main").toList(0).asJsObject
      val coord = fields.get("coord").toList(0).asJsObject

      val data = ArrayBuffer.empty[String]

      data+=coord.getFields("lat")(0).toString();
      data+=coord.getFields("lon")(0).toString();
      data+=fields.get("dt").toList(0).toString();
      data+=mainFields.getFields("pressure")(0).toString();
//      data+=mainFields.getFields("sea_level")(0).toString();
//      data+=mainFields.getFields("grnd_level")(0).toString();
      data+=mainFields.getFields("humidity")(0).toString();
      data+=mainFields.getFields("temp")(0).toString();

      val fileName = "/Users/dabugakov/Documents/Time-Series-Clustering-and-Classification/data/part-00000"
      val printWriter = new PrintWriter(new FileOutputStream(new File(fileName),true))
      printWriter.write(data.mkString(";")+'\n')
      printWriter.close()

//      println(data.mkString(";"))

      Thread.sleep(100000)
    }

  }

}
