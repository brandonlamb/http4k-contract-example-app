package verysecuresystems.external

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.BAD_GATEWAY
import org.http4k.lens.BodyLens
import org.http4k.lens.LensFailure

fun <T> HttpHandler.perform(request: Request, bodyLens: BodyLens<T>): T = this(request)
    .let {
        if (it.status.code > 399) {
            throw RemoteSystemProblem("entry logger", it.status)
        } else {
            try {
                bodyLens(it)
            } catch(e: LensFailure) {
                throw RemoteSystemProblem("entry logger", BAD_GATEWAY)
            }
        }
    }

data class RemoteSystemProblem(val name: String, val status: Status) : Exception("$name returned $status")