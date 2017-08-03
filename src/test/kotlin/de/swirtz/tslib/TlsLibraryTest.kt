package de.swirtz.tslib

import de.swirtz.tlslib.api.serverSocketFactory
import de.swirtz.tlslib.api.socketFactory
import de.swirtz.tlslib.examples.TLSServer
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.DataOutputStream
import javax.net.ssl.SSLSocketFactory
import kotlin.test.assertTrue

class TlsLibraryTest {

    lateinit var tlsServer: TLSServer

    @Before
    fun setup() = startServer()

    @Test
    fun clientServerCommTest() {
        val socket = createClientSocketFactory().createSocket("localhost", 9333)
        val content = "Hello World !"
        socket.use {
            DataOutputStream(it.getOutputStream()).use {
                content.split(" ").forEach(it::writeUTF)
            }
        }
        runBlocking { delay(1000) }
        assertEquals(content.replace(" ", ""), tlsServer.getLastMsg())
        assertTrue { socket.isClosed }
    }

    private fun createClientSocketFactory(): SSLSocketFactory {
        val fac = socketFactory {
            trustManager {
                open("certsandstores/myTruststore") withPass "123456" beingA "jks"
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
                open("certsandstores/clientkeystore") withPass "123456" beingA "jks"
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