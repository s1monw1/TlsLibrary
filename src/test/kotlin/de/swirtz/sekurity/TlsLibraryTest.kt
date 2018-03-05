package de.swirtz.sekurity

import de.swirtz.sekurity.api.serverSocketFactory
import de.swirtz.sekurity.api.socketFactory
import de.swirtz.sekurity.samples.TLSServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.DataOutputStream
import javax.net.ssl.SSLSocketFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TlsLibraryTest {

    lateinit var tlsServer: TLSServer

    @Before
    fun setup() = startServer()

    @After
    fun destroy() = tlsServer.stop()

    @Test
    fun clientServerConnTest() {
        val socket = createClientSocketFactory().createSocket("localhost", 9333)
        val content = "Hello World !"
        socket.use {
            DataOutputStream(it.getOutputStream()).use {
                content.split(" ").forEach(it::writeUTF)
            }
        }
        Thread.sleep(1000)
        assertEquals(content.replace(" ", ""), tlsServer.getLastMsg())
        assertTrue { socket.isClosed }
    }

    private fun createClientSocketFactory(): SSLSocketFactory {
        val fac = socketFactory {
            trustManager {
                open("src/test/resources/myTrustStore") withPass "123456" ofType "jks"
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
                open("src/test/resources/clientkeystore") withPass "123456" ofType "jks"
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