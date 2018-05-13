package de.swirtz.sekurity

import de.swirtz.sekurity.api.socketFactory
import de.swirtz.sekurity.samples.HttpsClientConnection
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals


class HttpsClientConnectionTest {
    private val port = 9911

    private val content = "<h1>Hello World</h1>"
    private lateinit var server: Server

    @Before
    fun startJetty() {
        server = Server(port).apply {
            handler = object : AbstractHandler() {
                override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
                    response.contentType = "text/html;charset=utf-8"
                    response.status = HttpServletResponse.SC_OK
                    baseRequest.isHandled = true
                    response.writer.println(content)
                }
            }

            val factory = SslContextFactory().apply {
                keyStorePath = "src/test/resources/clientkeystore"
                setKeyStorePassword("123456")
            }
            val sslConnector = ServerConnector(this, SslConnectionFactory(factory, "http/1.1"),
                    HttpConnectionFactory(HttpConfiguration().apply {
                        addCustomizer(SecureRequestCustomizer())
                    }))
            sslConnector.port = port
            connectors = arrayOf(sslConnector)

        }
        server.start()
    }

    @After
    fun stopJetty() = server.stop()

    @Test
    fun testCreateConnection() {
        val sf = socketFactory {
            trustManager {
                open("src/test/resources/myTrustStore", "jks") withPass "123456"
            }
            sockets {
                timeout = 10_000
            }
        }

        HttpsClientConnection(sf, 5000).
                createHttpsUrlConnection("https://localhost:$port/").inputStream.
                use {
                    val s = Scanner(it).useDelimiter("\\A")
                    if (s.hasNext()) assertEquals(content, s.next().trim())
                }
    }

}