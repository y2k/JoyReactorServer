package cc.joyreactor

import cc.joyreactor.core.*
import cc.joyreactor.core.JoyReactor.postWithComments
import com.google.gson.Gson
import org.jsoup.Jsoup
import spark.Spark.post
import java.io.InputStream
import java.lang.System.getProperty
import javax.servlet.MultipartConfigElement

fun main(args: Array<String>) {
    mutlipart("/posts") { Domain.getPostsWithNext(it) }
    mutlipart("/post") { Domain.getPostWithTopComments(it) }
    mutlipart("/profile") { Domain.getProfile(it) }
}

fun mutlipart(path: String, handler: (InputStream) -> Any) {
    post(path, { request, _ ->
        request.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement(getProperty("java.io.tmpdir")))
        request.raw().getPart("html").inputStream.use { handler(it) }
    }, Gson()::toJson)
}

object Domain {

    fun getProfile(stream: InputStream): Profile =
        Parsers.profile(stream.html())

    fun getPostsWithNext(stream: InputStream): Posts =
        stream.html().let { doc ->
            Posts(
                Parsers.parsePostsForTag(doc),
                Parsers.parseNewPageNumber(doc))
        }

    fun getPostWithTopComments(stream: InputStream): Post =
        postWithComments(stream.html(), TopComments(10))

    private fun InputStream.html() = Jsoup.parse(bufferedReader().readText())
}