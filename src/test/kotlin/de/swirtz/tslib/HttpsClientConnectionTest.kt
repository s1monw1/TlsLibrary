package de.swirtz.tslib

import de.swirtz.tlslib.api.socketFactory
import de.swirtz.tlslib.examples.HttpsClientConnection
import org.junit.Ignore
import org.junit.Test

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
                open("src/test/resources/truststore.jks") withPass "123456" beingA "jks"
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