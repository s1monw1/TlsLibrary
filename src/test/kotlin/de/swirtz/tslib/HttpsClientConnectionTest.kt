package de.swirtz.tslib

import de.swirtz.tlslib.api.socketFactory
import de.swirtz.tlslib.server.HttpsClientConnection
import org.junit.Ignore
import org.junit.Test
import java.nio.file.Paths

/**
 *
 * File created on 03.08.2017.
 */

class HttpsClientConnectionTest {

    @Ignore("Mock Server!")
    @Test
    fun testCreateConnection() {
        val sf = socketFactory {
            trustManager {
                storeFile = Paths.get("src/test/resources/truststore.jks")
                password = "12345678"
                fileType = "jks"
            }
            sockets {
                timeout = 10_000
            }
        }
        val inputStream = HttpsClientConnection(sf, 5000).createHttpsUrlConnection("https://192.168.3.214/connector.sds").inputStream
        inputStream.use {
            val s = java.util.Scanner(it).useDelimiter("\\A")
            if (s.hasNext()) println(s.next())
        }
    }
}