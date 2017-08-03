package de.swirtz.tslib

import de.swirtz.tlslib.api.serverSocketFactory
import de.swirtz.tlslib.api.socketFactory
import de.swirtz.tlslib.server.TLSServer
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.DataOutputStream
import java.nio.file.Paths
import javax.net.ssl.SSLSocketFactory
import kotlin.test.assertTrue

class TlsLibraryTest {

    lateinit var tlsServer: TLSServer

    @Before
    fun setup() = startServer()

    @Test
    fun firstTest() {
        val socket = createClientSocketFactory().createSocket("localhost", 9333)
        socket.use {
            DataOutputStream(it.getOutputStream()).use {
                it.writeUTF("Hello ")
                it.writeUTF("World")
                it.writeUTF("!")
            }
        }
        runBlocking { delay(1000) }
        assertEquals("Hello World!", tlsServer.getLastMsg())
        assertTrue { socket.isClosed }
    }

    private fun createClientSocketFactory(): SSLSocketFactory {
        val fac = socketFactory {
            trustManager {
                storeFile = Paths.get("certsandstores/myTruststore")
                password = "123456"
                fileType = "jks"
            }
            sockets {
                timeout = 10_000
            }
        }
        return fac
    }

    private fun startServer() {
        val fac = serverSocketFactory {
            keyManager {
                storeFile = Paths.get("certsandstores/clientkeystore")
                password = "123456"
                fileType = "jks"
            }
            sockets {
                clientAuth = true
                timeout = 10_000
            }
        }

        tlsServer = TLSServer(9333, fac)
        tlsServer.start()
    }
}