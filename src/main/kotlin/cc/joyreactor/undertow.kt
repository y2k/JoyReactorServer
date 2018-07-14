package cc.joyreactor

import cc.joyreactor.core.JoyReactor.postWithComments
import cc.joyreactor.core.TopComments
import com.google.gson.Gson
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.server.RoutingHandler
import io.undertow.server.handlers.form.EagerFormParsingHandler
import io.undertow.server.handlers.form.FormDataParser
import io.undertow.server.handlers.form.FormParserFactory
import io.undertow.server.handlers.form.MultiPartParserDefinition
import io.undertow.util.Headers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import cc.joyreactor.core.Parsers as P

fun main(args: Array<String>) =
    Undertow
        .builder()
        .addHttpListener(4567, "0.0.0.0")
        .setHandler(Handlers.routing()
            .get("/info") { it.responseSender.send("JoyReactor Parser (undertow) - ${Date()}") }
            .apply {
                service("/posts") { html ->
                    mapOf(
                        "posts" to P.parsePostsForTag(html),
                        "nextPage" to P.parseNewPageNumber(html))
                }
                service("/post") { html ->
                    postWithComments(html, TopComments(20))
                }
                service("/profile", P::profile)
                service("/tags", P::readingTags)
                service("/messages") { html ->
                    P.getMessages(html)
                        .let { (messages, nextPage) ->
                            mapOf(
                                "messages" to messages,
                                "nextPage" to nextPage)
                        }
                }
            }
        )
        .build()
        .start()

private fun RoutingHandler.service(path: String, function: (Document) -> Any): RoutingHandler =
    post(path,
        EagerFormParsingHandler(
            FormParserFactory.builder()
                .addParsers(MultiPartParserDefinition())
                .build())
            .setNext { exchange ->
                val json = exchange
                    .getAttachment(FormDataParser.FORM_DATA)
                    .get("html")
                    .first.path.toFile()
                    .inputStream().use { Jsoup.parse(it, null, "") }
                    .let(function)
                    .let(Gson()::toJson)
                exchange.responseHeaders.put(Headers.CONTENT_TYPE, "application/json")
                exchange.responseSender.send(json)
            })