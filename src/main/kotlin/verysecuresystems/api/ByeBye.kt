package verysecuresystems.api

import org.http4k.contract.ContractRoute
import org.http4k.contract.RouteMeta
import org.http4k.contract.bind
import org.http4k.contract.meta
import org.http4k.contract.query
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.format.Jackson.auto
import org.http4k.lens.Query
import verysecuresystems.Inhabitants
import verysecuresystems.Message
import verysecuresystems.Username
import verysecuresystems.external.EntryLogger

object ByeBye {
    private val username = Query.map(::Username).required("username")
    private val message = Body.auto<Message>().toLens()

    fun route(inhabitants: Inhabitants, entryLogger: EntryLogger): ContractRoute {

        val userExit: HttpHandler = {
            val exiting = username(it)
            if (inhabitants.remove(exiting)) {
                entryLogger.exit(exiting)
                Response(ACCEPTED).with(message of Message("processing"))
            } else Response(NOT_FOUND).with(message of Message("User is not inside building"))
        }

        return (
            "/bye"
                query username
                to POST
                bind userExit
                meta RouteMeta("User exits the building")
                .returning("Exit granted" to ACCEPTED)
                .returning("User is not inside building" to NOT_FOUND)
                .returning("Incorrect key" to UNAUTHORIZED)
            )
    }
}