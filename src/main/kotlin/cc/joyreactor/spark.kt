package cc.joyreactor

import cc.joyreactor.core.*
import cc.joyreactor.core.JoyReactor.postWithComments
import spark.Spark.get
import java.util.*
import cc.joyreactor.core.Parsers as P

fun main(args: Array<String>) {
    get("/info") { _, _ -> "JoyReactor Parser - ${Date()}" }

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