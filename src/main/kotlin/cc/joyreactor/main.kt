package cc.joyreactor

import cc.joyreactor.core.*
import cc.joyreactor.core.JoyReactor.postWithComments
import java.io.InputStream
import cc.joyreactor.core.Parsers as P

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        mutlipart("/posts", ::getPostsWithNext)
        mutlipart("/post", ::getPostWithTopComments)
        multipartDocument("/profile", P::profile)
        multipartDocument("/tags", P::readingTags)
        multipartDocument("/messages", { html ->
            html.let(P::getMessages)
                .let { mapOf("messages" to it.first, "nextPage" to it.second) }
        })
    }

    private fun getPostsWithNext(stream: InputStream): Posts =
        stream.html().let { doc ->
            Posts(
                P.parsePostsForTag(doc),
                P.parseNewPageNumber(doc))
        }

    private fun getPostWithTopComments(stream: InputStream): Post =
        postWithComments(stream.html(), TopComments(20))
}