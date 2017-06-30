package cc.joyreactor

import cc.joyreactor.core.*
import cc.joyreactor.core.JoyReactor.postWithComments
import java.io.InputStream
import cc.joyreactor.core.Parsers as P

fun main(args: Array<String>) {
    mutlipart("/posts", Domain::getPostsWithNext)
    mutlipart("/post", Domain::getPostWithTopComments)
    mutlipart("/profile", Domain::getProfile)
    mutlipart("/tags", Domain::userTags)
}

object Domain {

    fun userTags(stream: InputStream): List<Tag> =
        stream.html().let(P::readingTags)

    fun getProfile(stream: InputStream): Profile =
        stream.html().let(P::profile)

    fun getPostsWithNext(stream: InputStream): Posts =
        stream.html().let { doc ->
            Posts(
                P.parsePostsForTag(doc),
                P.parseNewPageNumber(doc))
        }

    fun getPostWithTopComments(stream: InputStream): Post =
        postWithComments(stream.html(), TopComments(20))
}