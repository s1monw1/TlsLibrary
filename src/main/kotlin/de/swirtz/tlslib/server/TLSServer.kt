package de.swirtz.tlslib.server

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.DataInputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ServerSocketFactory

/**
 * Creates Socket with supplied createSocketFactory and waits for a client to connect
 */
class TLSServer(val port: Int, val socketFactory: ServerSocketFactory) {

    private var read = StringBuilder()

    private val running = AtomicBoolean(true)
    fun getLastMsg() = read.toString()

    fun start() = launch(CommonPool) {
        while (running.get()) {
            val accept = socketFactory.createServerSocket(port).accept()
            println("accepted socket $accept")
            accept.getInputStream().use {
                DataInputStream(it).use { d ->
                    var readUTF = d.readUTF()
                    while (readUTF != null) {
                        println("Read: '$readUTF'")
                        read.append(readUTF)
                        readUTF = d.readUTF()
                    }
                }
            }
        }

    }

    fun stop() = running.set(false)


}