package de.swirtz.sekurity.samples

import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import java.io.DataInputStream
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ServerSocketFactory

/**
 * Creates Socket with supplied socketFactory and waits for a client to connect
 */
class TLSServer(private val port: Int, private val socketFactory: ServerSocketFactory) {
    private val LOG = LoggerFactory.getLogger(TLSServer::class.java)

    private var read = StringBuilder()

    private val running = AtomicBoolean(true)
    private lateinit var socket: ServerSocket

    fun getLastMsg() = read.toString()

    fun start() {
        socket = socketFactory.createServerSocket(port)
        LOG.debug("started server on $port")
        launch {
            while (running.get()) {
                LOG.debug("wait for client to connect")
                with(socket.accept()) {
                    LOG.debug("accepted socket $this")
                    DataInputStream(getInputStream()).use { d ->
                        var readUTF = d.readUTF()
                        while (readUTF != null) {
                            LOG.debug("Read: '$readUTF'")
                            read.append(readUTF)
                            readUTF = d.readUTF()
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        running.set(false)
        socket.close()
    }


}