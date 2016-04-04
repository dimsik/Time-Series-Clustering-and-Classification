import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

object Main {

  def main(args: Array[String]) {
    val httpClient = new DefaultHttpClient();
    val httpResponse = httpClient.execute(new HttpGet(""));
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
