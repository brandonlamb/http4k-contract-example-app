package cdc

import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.junit.Ignore
import verysecuresystems.EmailAddress
import verysecuresystems.Username

/**
 * Contract implementation for the real user directory service. Extra steps might be required here to setup/teardown
 * test data.
 */
@Ignore // this would not be ignored in reality
class RealUserDirectoryTest : UserDirectoryContract(
    SetHostFrom(Uri.of("http://userdirectory.com")).then(OkHttp())
) {

    // real test data would be set up here for the required environment
    override val username = Username("Elon Musk")
    override val email = EmailAddress("elon@tesla.com")
}
