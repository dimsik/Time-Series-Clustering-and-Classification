import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

object Main {

  def main(args: Array[String]) {
    val httpClient = new DefaultHttpClient();
    val httpResponse = httpClient.execute(new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=London&APPID=8816ee47eeaf32a2cc592b0ca57ac742"));
    val entity = httpResponse.getEntity()
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent()
      content = io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    httpClient.getConnectionManager().shutdown()
    println(content)
  }



}
