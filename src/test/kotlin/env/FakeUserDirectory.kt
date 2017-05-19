package env

import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import verysecuresystems.Id
import verysecuresystems.User
import verysecuresystems.external.UserDirectory.Companion.Contract.Create
import verysecuresystems.external.UserDirectory.Companion.Contract.Delete
import verysecuresystems.external.UserDirectory.Companion.Contract.Lookup
import verysecuresystems.external.UserDirectory.Companion.Contract.UserList

class FakeUserDirectory {

    private val users = mutableMapOf<Id, User>()

    fun contains(newUser: User) = users.put(newUser.id, newUser)

    fun reset() = users.clear()

    val routes = listOf(
        Create.route bind {
            val form = Create.form(it)
            val newUser = User(Id(users.size), Create.username(form), Create.email(form))
            users.put(newUser.id, newUser)
            Response(CREATED).with(Create.response of newUser)
        },
        Delete.route bind {
            id ->
            {
                users.remove(id)?.let {
                    Response(ACCEPTED).with(Delete.response of it)
                } ?: Response(NOT_FOUND)
            }
        },
        Lookup.route bind {
            username ->
            {
                users.values
                    .filter { it.name == username }
                    .firstOrNull()
                    ?.let { Response(OK).with(Lookup.response of it) }
                    ?: Response(NOT_FOUND)
            }
        },
        UserList.route bind {
            Response(OK).with(UserList.response of users.values.toList())
        }
    )
}