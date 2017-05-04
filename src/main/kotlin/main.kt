import cc.joyreactor.core.Parsers
import cc.joyreactor.core.Posts
import com.google.gson.Gson
import org.jsoup.Jsoup
import spark.Spark.post
import javax.servlet.MultipartConfigElement

fun main(args: Array<String>) {
    post("/posts", { request, _ ->
        request.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/temp"))
        val html = request.raw().getPart("html").inputStream.bufferedReader().readText()
        Domain.parse(html)
    }, Gson()::toJson)
}

object Domain {

    fun parse(html: String): Posts =
        Jsoup.parse(html)
            .let { doc ->
                Posts(
                    Parsers.parsePostsForTag(doc),
                    Parsers.parseNewPageNumber(doc))
            }
}