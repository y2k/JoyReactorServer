package cc.joyreactor

import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spark.Spark
import java.io.InputStream
import javax.servlet.MultipartConfigElement

/**
 * Created by y2k on 01/07/2017.
 **/

fun InputStream.html(): Document =
    Jsoup.parse(this, null, "")

fun mutlipart(path: String, handler: (InputStream) -> Any) {
    Spark.post(path, { request, _ ->
        request.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement(System.getProperty("java.io.tmpdir")))
        request.raw().getPart("html").inputStream.use { handler(it) }
    }, Gson()::toJson)
}